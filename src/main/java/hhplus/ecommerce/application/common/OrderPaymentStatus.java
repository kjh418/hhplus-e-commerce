package hhplus.ecommerce.application.common;

import lombok.Getter;

@Getter
public enum OrderPaymentStatus {
    PENDING("대기"),
    COMPLETED("완료"),
    CANCELLED("취소"),
    FAILED("실패");

    private final String status;

    OrderPaymentStatus(String status) {
        this.status = status;
    }
}
