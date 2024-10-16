package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.domain.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String message;
    private PaymentStatus status;
}
