package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.payment.AccountPointService;
import hhplus.ecommerce.application.payment.dto.PaymentDto;
import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class AccountPointController {

    private final AccountPointService accountPointService;

    @GetMapping("/{userId}/balance")
    public ResponseEntity<UserBalanceResponse> getBalance(@PathVariable Long userId) {
        UserBalanceResponse response = accountPointService.getBalance(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/charge")
    public ResponseEntity<UserBalanceResponse> chargePoints(
            @PathVariable Long userId,
            @Valid @RequestBody PaymentDto paymentDto) {
        UserBalanceResponse response = accountPointService.chargePoints(userId, paymentDto, paymentDto.getOrderId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<PaymentHistory>> getPaymentHistory(@PathVariable Long userId) {
        List<PaymentHistory> history = accountPointService.getPaymentHistory(userId);
        return ResponseEntity.ok(history);
    }
}
