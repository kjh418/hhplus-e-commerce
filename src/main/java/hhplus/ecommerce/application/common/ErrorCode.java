package hhplus.ecommerce.application.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_BALANCE("잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED("결제에 실패하였습니다.", HttpStatus.PAYMENT_REQUIRED),
    MAXIMUM_CHARGE_LIMIT_EXCEEDED("한 번에 충전할 수 있는 최대 금액은 20만원입니다.", HttpStatus.BAD_REQUEST),
    ITEM_OUT_OF_STOCK("선택하신 상품은 재고가 없습니다. 현재 재고: %s", HttpStatus.BAD_REQUEST),
    DUPLICATE_REQUEST("중복된 요청입니다.", HttpStatus.CONFLICT),
    POINTS_CHARGED_SUCCESSFULLY("포인트가 성공적으로 충전되었습니다. 충전 금액: %s, 총 포인트: %s", HttpStatus.OK),
    INVALID_POINT_AMOUNT("유효하지 않은 포인트 금액입니다.", HttpStatus.BAD_REQUEST),
    NEGATIVE_POINT_AMOUNT("충전할 금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_AMOUNT_MISMATCH("결제 금액이 주문 금액과 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    MISSING_ORDER_ID("주문 ID가 누락되었습니다.", HttpStatus.BAD_REQUEST),
    MISSING_PAYMENT_AMOUNT("결제 금액이 누락되었습니다.", HttpStatus.BAD_REQUEST),
    GENERIC_SERVER_ERROR("서버에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}
