package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.payment.PaymentService;
import hhplus.ecommerce.application.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> payment(@PathVariable Long orderId, @RequestParam Long userId, @RequestParam BigDecimal paymentAmount) {
        PaymentResponse response = paymentService.processPayment(orderId, userId, paymentAmount);
        return ResponseEntity.ok(response);
    }
}
