package hhplus.ecommerce.application.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("ERR001", "사용자가 존재하지 않습니다."),
    INSUFFICIENT_BALANCE("ERR002", "잔액이 부족합니다."),
    ORDER_NOT_FOUND("ERR003", "주문을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND("ERR004", "상품이 존재하지 않습니다."),
    PAYMENT_FAILED("ERR005", "결제에 실패하였습니다."),
    MAXIMUM_CHARGE_LIMIT_EXCEEDED("ERR006", "한 번에 충전할 수 있는 최대 금액은 20만원입니다."),
    ITEM_OUT_OF_STOCK("ERR007", "선택하신 상품은 재고가 없습니다. 현재 재고: %s"),
    ORDER_ALREADY_COMPLETED("ERR008", "이미 결제가 완료된 주문입니다."),
    POINTS_CHARGED_SUCCESSFULLY("ERR009", "포인트가 성공적으로 충전되었습니다. 충전 금액: %s, 총 포인트: %s"),
    INVALID_POINT_AMOUNT("ERR010", "유효하지 않은 포인트 금액입니다."),
    NEGATIVE_POINT_AMOUNT("ERR011", "충전할 금액은 0보다 커야 합니다."),
    PAYMENT_AMOUNT_MISMATCH("ERR012", "결제 금액이 주문 금액과 일치하지 않습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
