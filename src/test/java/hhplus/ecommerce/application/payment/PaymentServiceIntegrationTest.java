package hhplus.ecommerce.application.payment;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        user = new Users(1L, "홍길동", "서울특별시 강남구", "010-1234-1234", LocalDateTime.now());
        user = usersRepository.save(user);

        PointAccount pointAccount = new PointAccount(null, user.getId(), new BigDecimal("50000"));
        pointAccountRepository.save(pointAccount);

        // 주문 설정
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

        // 주문 상태가 COMPLETED로 변경되었는지 확인
        Orders updatedOrder = ordersRepository.findById(order.getId()).orElse(null);
        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());

        // 결제 기록이 저장되었는지 확인
        Optional<PaymentHistory> paymentHistory = paymentHistoryRepository.findByUserId(user.getId()).stream().findFirst();
        assertThat(paymentHistory).isPresent();
        assertThat(paymentHistory.get().getOrderId()).isEqualTo(order.getId());
        assertThat(paymentHistory.get().getPoints()).isEqualTo(paymentAmount);
        assertThat(paymentHistory.get().getType()).isEqualTo(PointType.USE);

        // 포인트 잔액이 차감되었는지 확인
        BigDecimal remainingPoints = userPointRepository.findCurrentPointsByUserId(user.getId());
        assertEquals(new BigDecimal("30000").setScale(2), remainingPoints.setScale(2));
    }

    @Test
    public void 포인트_부족으로_결제_실패_테스트() {
        BigDecimal paymentAmount = new BigDecimal("20000.00");

        PaymentResponse response = paymentService.processPayment(order.getId(), user.getId(), paymentAmount);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
        assertEquals("포인트가 부족합니다.", response.getMessage());

        // 주문 상태는 여전히 PENDING이어야 함
        Orders pendingOrder = ordersRepository.findById(order.getId()).orElse(null);
        assertNotNull(pendingOrder);
        assertEquals(OrderStatus.PENDING, pendingOrder.getStatus());

        // 결제 실패 기록 확인
        Optional<PaymentHistory> paymentHistory = paymentHistoryRepository.findByUserId(user.getId()).stream().findFirst();
        assertThat(paymentHistory).isEmpty();  // 결제 실패 시 결제 기록이 없어야 함

        // 포인트 잔액은 변경되지 않아야 함
        BigDecimal remainingPoints = userPointRepository.findCurrentPointsByUserId(user.getId());
        assertEquals(new BigDecimal("50000.00"), remainingPoints);  // 잔액은 변하지 않음
    }
}