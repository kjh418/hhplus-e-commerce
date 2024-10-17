package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.payment.AccountPointService;
import hhplus.ecommerce.application.user.UserBalanceResponse;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/points")
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
            @RequestParam BigDecimal amount) {
        UserBalanceResponse response = accountPointService.chargePoints(userId, amount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<PaymentHistory>> getPaymentHistory(@PathVariable Long userId) {
        List<PaymentHistory> history = accountPointService.getPaymentHistory(userId);
        return ResponseEntity.ok(history);
    }

    public AccountPointController(AccountPointService accountPointService) {
        this.accountPointService = accountPointService;
    }
}
