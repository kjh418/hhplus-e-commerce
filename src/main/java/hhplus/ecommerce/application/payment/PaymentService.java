package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.payment.Payment;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.PaymentRepository;
import hhplus.ecommerce.infrastructure.repository.UserPointRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public PaymentResponse processPayment(Long orderId, Long userId, BigDecimal paymentAmount) {

        // 사용자 확인
        Users user = usersRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        // 주문 확인
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new NoSuchElementException("주문을 찾을 수 없습니다."));

        // 결제 완료된 주문인지 확인
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 결제가 완료된 주문입니다.");
        }

        // 주문 금액과 결제할 금액의 일치 확인
        if (order.getTotalAmount().compareTo(paymentAmount) != 0) {
            throw new IllegalArgumentException("결제 금액이 주문 금액과 일치하지 않습니다.");
        }

        // 포인트 잔액 확인
        BigDecimal currentPoints = userPointRepository.findCurrentPointsByUserId(user.getId());

        if (currentPoints == null) {
            throw new IllegalStateException("잔액이 존재하지 않습니다.");
        }

        if (currentPoints.compareTo(paymentAmount) < 0) {
            Payment failedPayment = new Payment(userId, orderId, paymentAmount, PaymentStatus.FAILED, LocalDateTime.now());
            paymentRepository.save(failedPayment);
            return new PaymentResponse("포인트가 부족합니다.", PaymentStatus.FAILED);
        }

        // 결제 진행
        BigDecimal newBalance = currentPoints.subtract(paymentAmount);
        userPointRepository.updatePoints(userId, newBalance);

        // 주문 상태 변경
        order.completeOrder();
        orderRepository.save(order);

        // 결제 기록 저장
        Payment payment = new Payment(userId, orderId, paymentAmount, PaymentStatus.SUCCESS, LocalDateTime.now());
        paymentRepository.save(payment);

        return new PaymentResponse("결제가 완료되었습니다.", PaymentStatus.SUCCESS);
    }
}
