package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.common.ErrorResponse;
import hhplus.ecommerce.application.user.UserBalanceResponse;
import hhplus.ecommerce.application.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/users")
public class UserController {

    @Operation(summary = "잔액 충전 API", description = "사용자의 잔액을 충전합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "잔액 충전 성공"),
        @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음", content = @Content()),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content())
    })
    @PostMapping("/{userId}/recharge")
    public ResponseEntity<Object>  rechargeBalance(@PathVariable Long userId, @RequestParam BigDecimal amount){

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("충전 금액은 0보다 커야 합니다."));
        }

        BigDecimal MAX_RECHARGE_AMOUNT = new BigDecimal("200000");
        if (amount.compareTo(MAX_RECHARGE_AMOUNT) > 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("1회 충전 금액은 20만원을 초과할 수 없습니다."));
        }

        UserDto mockUser = new UserDto(userId, "홍길동", "서울시 강남구", "010-1234-5678", LocalDateTime.now());
        BigDecimal mockBalance = new BigDecimal("50000").add(amount);
        UserBalanceResponse response = new UserBalanceResponse(mockUser, mockBalance);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "잔액 조회 API", description = "사용자의 잔액을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "잔액 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음", content = @Content()),
    })
    @GetMapping("/{userId}/balance")
    public ResponseEntity<UserBalanceResponse> getBalance(@PathVariable Long userId) {
        String mockUserName = "홍길동";
        BigDecimal mockBalance = new BigDecimal("50000");
        UserDto userDto = new UserDto(userId, mockUserName, "서울시 강남구", "010-1234-5678", LocalDateTime.now());
        UserBalanceResponse response = new UserBalanceResponse(userDto, mockBalance);
        return ResponseEntity.ok(response);
    }
}
