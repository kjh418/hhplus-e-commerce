package hhplus.ecommerce.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("대기"),      // 주문이 생성되었으나 결제 대기 중인 상태
    COMPLETED("완료"),    // 주문이 완료된 상태 (결제 완료 후)
    CANCELLED("취소");    // 주문이 취소된 상태

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
