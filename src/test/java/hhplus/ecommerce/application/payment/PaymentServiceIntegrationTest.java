package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.common.ErrorCode;
import hhplus.ecommerce.application.payment.dto.PaymentResponse;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import hhplus.ecommerce.domain.payment.PointAccount;
import hhplus.ecommerce.domain.payment.PointType;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private UserPointRepository userPointRepository;
    @Autowired
    private PointAccountRepository pointAccountRepository;
    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    private Users user;
    private Orders order;

    @BeforeEach
    public void setUp() {
        user = new Users(null, "홍길동", "서울특별시 강남구", "010-1234-1234", LocalDateTime.now());
        user = usersRepository.save(user);

        PointAccount pointAccount = new PointAccount(null, user.getId(), new BigDecimal("50000.00"));
        pointAccountRepository.save(pointAccount);

        order = new Orders(user.getId(), new BigDecimal("20000.00"), OrderStatus.PENDING, LocalDateTime.now());
        order = ordersRepository.save(order);
    }

    @Test
    public void 결제_성공_테스트() {
        BigDecimal paymentAmount = new BigDecimal("20000.00");

        PaymentResponse response = paymentService.processPayment(order.getId(), user.getId(), paymentAmount);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals("결제가 완료되었습니다.", response.getMessage());

        Orders updatedOrder = ordersRepository.findById(order.getId()).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());

        Optional<PaymentHistory> paymentHistory = paymentHistoryRepository.findByUserId(user.getId()).stream().findFirst();
        assertTrue(paymentHistory.isPresent());
        assertEquals(paymentHistory.get().getOrderId(), order.getId());
        assertEquals(paymentHistory.get().getPoints(), paymentAmount);
        assertEquals(paymentHistory.get().getType(), PointType.USE);

        BigDecimal remainingPoints = userPointRepository.findCurrentPointsByUserId(user.getId());
        assertEquals(new BigDecimal("30000.00"), remainingPoints.setScale(2));
    }

    @Test
    public void 포인트_부족으로_결제_실패_테스트() {
        BigDecimal paymentAmount = new BigDecimal("60000.00");

        PaymentResponse response = paymentService.processPayment(order.getId(), user.getId(), paymentAmount);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
        assertEquals("잔액이 부족합니다.", response.getMessage());

        Orders pendingOrder = ordersRepository.findById(order.getId()).orElse(null);
        assertNotNull(pendingOrder);
        assertEquals(OrderStatus.PENDING, pendingOrder.getStatus());

        Optional<PaymentHistory> paymentHistory = paymentHistoryRepository.findByUserId(user.getId()).stream().findFirst();
        assertTrue(paymentHistory.isEmpty());

        BigDecimal remainingPoints = userPointRepository.findCurrentPointsByUserId(user.getId());
        assertEquals(new BigDecimal("50000.00"), remainingPoints);
    }

    @Test
    public void 중복_결제_요청_테스트() {
        BigDecimal paymentAmount = new BigDecimal("20000.00");

        PaymentResponse firstResponse = paymentService.processPayment(order.getId(), user.getId(), paymentAmount);
        assertEquals(PaymentStatus.SUCCESS, firstResponse.getStatus());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(order.getId(), user.getId(), paymentAmount);
        });

        assertEquals("중복된 요청입니다.", exception.getMessage());

        Orders completedOrder = ordersRepository.findById(order.getId()).orElse(null);
        assertNotNull(completedOrder);
        assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus());

        long paymentHistoryCount = paymentHistoryRepository.findByUserId(user.getId()).stream().count();
        assertEquals(1, paymentHistoryCount);

        BigDecimal remainingPoints = userPointRepository.findCurrentPointsByUserId(user.getId());
        assertEquals(new BigDecimal("30000.00"), remainingPoints.setScale(2));
    }

    @Test
    public void 결제_금액_불일치_테스트() {
        BigDecimal incorrectAmount = new BigDecimal("25000.00");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(order.getId(), user.getId(), incorrectAmount);
        });

        assertEquals(ErrorCode.PAYMENT_AMOUNT_MISMATCH.getMessage(), exception.getMessage());

        Orders pendingOrder = ordersRepository.findById(order.getId()).orElse(null);
        assertNotNull(pendingOrder);
        assertEquals(OrderStatus.PENDING, pendingOrder.getStatus());

        BigDecimal remainingPoints = userPointRepository.findCurrentPointsByUserId(user.getId());
        assertEquals(new BigDecimal("50000.00"), remainingPoints);
    }
}
