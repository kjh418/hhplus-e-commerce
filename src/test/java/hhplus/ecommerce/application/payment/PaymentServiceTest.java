package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.payment.dto.PaymentResponse;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.payment.Payment;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.PaymentRepository;
import hhplus.ecommerce.infrastructure.repository.UserPointRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private OrdersRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Long userId;
    private Long orderId;
    private BigDecimal paymentAmount;
    private Users user;
    private Orders order;

    @BeforeEach
    void setUp() {
        userId = 1L;
        orderId = 1L;
        paymentAmount = new BigDecimal("10000");
        user = new Users(userId, "홍길동", "서울시 강남구", "01012341234", LocalDateTime.now());
        order = new Orders(orderId, userId, paymentAmount, OrderStatus.PENDING, LocalDateTime.now());
    }

    @Test
    void 포인트_부족으로_결제_실패_시_결제_상태_실패_처리_후_이력_저장() {
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userPointRepository.findCurrentPointsByUserId(userId)).thenReturn(new BigDecimal("5000"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        PaymentResponse response = paymentService.processPayment(orderId, userId, paymentAmount);

        assertEquals("포인트가 부족합니다.", response.getMessage());
        assertEquals(PaymentStatus.FAILED, response.getStatus());

        // 이력 저장 확인
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 존재하지_않는_주문일_경우_예외_처리() {
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            paymentService.processPayment(orderId, userId, paymentAmount);
        });

        assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 존재하지_않는_사용자일_경우_예외_처리() {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            paymentService.processPayment(orderId, userId, paymentAmount);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 이미_완료된_결제인_경우_예외_처리() {
        order = new Orders(orderId, userId, new BigDecimal("10000"), OrderStatus.COMPLETED, LocalDateTime.now());
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(orderId, userId, paymentAmount);
        });

        assertEquals("이미 결제가 완료된 주문입니다.", exception.getMessage());
    }

    @Test
    void 결제하려는_금액과_주문_금액이_일치하지_않는_경우_예외_처리() {
        BigDecimal paymentAmount = new BigDecimal("5000");
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(orderId, userId, paymentAmount);
        });

        assertEquals("결제 금액이 주문 금액과 일치하지 않습니다.", exception.getMessage());
    }
}