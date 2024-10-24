package hhplus.ecommerce.domain.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private BigDecimal balance; // 포인트 잔액

    @Version
    private Long version;

    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }

    public PointAccount(Long id, Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
