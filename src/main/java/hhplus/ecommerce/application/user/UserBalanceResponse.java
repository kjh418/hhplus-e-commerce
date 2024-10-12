package hhplus.ecommerce.application.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {
    private UserDto user;
    private BigDecimal balance;
}
