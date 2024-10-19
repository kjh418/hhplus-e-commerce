package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.payment.PaymentService;
import hhplus.ecommerce.application.payment.dto.PaymentResponse;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> payment(@PathVariable Long orderId, @RequestParam Long userId, @RequestParam BigDecimal paymentAmount) {
        try {
            PaymentResponse response = paymentService.processPayment(orderId, userId, paymentAmount);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PaymentResponse(e.getMessage(), PaymentStatus.FAILED));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PaymentResponse(e.getMessage(), PaymentStatus.FAILED));
        }
    }


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
