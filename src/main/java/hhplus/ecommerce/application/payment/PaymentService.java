package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.payment.dto.PaymentResponse;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.payment.Payment;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import hhplus.ecommerce.domain.payment.PointType;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.*;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UsersRepository usersRepository;
    private final UserPointRepository userPointRepository;
    private final OrdersRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    public PaymentResponse processPayment(Long orderId, Long userId, BigDecimal paymentAmount) {

        // 사용자 확인
        Users user = getUserOrThrow(userId);

        // 주문 확인
        Orders order = getOrderOrThrow(orderId);

        // 결제 상태, 금액 확인
        validateOrderForPayment(order, paymentAmount);

        // 포인트 잔액 확인
        BigDecimal currentPoints = getCurrentPointsWithLock(user.getId());

        // 포인트 잔액 확인, 결제 처리
        return handlePayment(userId, order, paymentAmount, currentPoints);
    }

    private PaymentResponse handlePayment(Long userId, Orders order, BigDecimal paymentAmount, BigDecimal currentPoints) {

        if (currentPoints == null || currentPoints.compareTo(paymentAmount) < 0) {
            saveFailedPayment(userId, order.getId(), paymentAmount);
            return new PaymentResponse("포인트가 부족합니다.", PaymentStatus.FAILED);
        }

        // 성공적인 결제 기록만 PointType에 기록
        saveSuccessfulPaymentHistory(userId, order.getId(), paymentAmount);
        processSuccessfulPayment(userId, order, paymentAmount, currentPoints);
        return new PaymentResponse("결제가 완료되었습니다.", PaymentStatus.SUCCESS);
    }

    private void saveFailedPayment(Long userId, Long orderId, BigDecimal paymentAmount) {
        Payment failedPayment = new Payment(userId, orderId, paymentAmount, PaymentStatus.FAILED, LocalDateTime.now());
        paymentRepository.save(failedPayment);

        Orders order = getOrderOrThrow(orderId);
        order.cancelOrder();
        orderRepository.save(order);
    }

    private Users getUserOrThrow(Long userId) {
        return usersRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    private Orders getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다."));
    }

    private void validateOrderForPayment(Orders order, BigDecimal paymentAmount) {
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 결제가 완료된 주문입니다.");
        }

        if (order.getTotalAmount().compareTo(paymentAmount) != 0) {
            throw new IllegalArgumentException("결제 금액이 주문 금액과 일치하지 않습니다.");
        }
    }

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public BigDecimal getCurrentPointsWithLock(Long userId) {
        return userPointRepository.findCurrentPointsByUserId(userId);
    }

    private void saveSuccessfulPaymentHistory(Long userId, Long orderId, BigDecimal paymentAmount) {
        PaymentHistory history = new PaymentHistory(userId, orderId, paymentAmount, PointType.USE, LocalDateTime.now());
        paymentHistoryRepository.save(history);
    }

    private void processSuccessfulPayment(Long userId, Orders order, BigDecimal paymentAmount, BigDecimal currentPoints) {
        BigDecimal newBalance = currentPoints.subtract(paymentAmount);
        updatePointsWithLock(userId, newBalance);
        order.completeOrder(); // 주문 상태 변경
        orderRepository.save(order); // 주문 저장
        Payment payment = new Payment(userId, order.getId(), paymentAmount, PaymentStatus.SUCCESS, LocalDateTime.now());
        paymentRepository.save(payment); // 결제 기록 저장
    }

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void updatePointsWithLock(Long userId, BigDecimal newBalance) {
        userPointRepository.updatePoints(userId, newBalance);
    }
}
