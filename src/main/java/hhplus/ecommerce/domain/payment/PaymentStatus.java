package hhplus.ecommerce.domain.payment;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("결제 대기"),   // 결제 대기 중
    SUCCESS("결제 성공"),   // 결제가 성공적으로 완료됨
    FAILED("결제 실패");    // 결제가 실패함

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}