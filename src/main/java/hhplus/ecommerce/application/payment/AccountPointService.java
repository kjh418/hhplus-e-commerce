package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.user.UserBalanceResponse;
import hhplus.ecommerce.application.user.UserDto;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PointAccount;
import hhplus.ecommerce.domain.payment.PointType;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.PaymentHistoryRepository;
import hhplus.ecommerce.infrastructure.repository.PointAccountRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountPointService {

    private static final BigDecimal MAX_CHARGE_AMOUNT = new BigDecimal("200000");

    private final UsersRepository userRepository;
    private final PointAccountRepository pointAccountRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public UserBalanceResponse getBalance(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        PointAccount account = pointAccountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PointAccount newAccount = new PointAccount(null, userId, BigDecimal.ZERO);
                    pointAccountRepository.save(newAccount);
                    return newAccount;
                });

        return new UserBalanceResponse(
                new UserDto(user.getId(), user.getName(), user.getAddress(), user.getPhoneNumber(), user.getCreatedAt()),
                account.getBalance()
        );
    }

    public UserBalanceResponse chargePoints(Long userId, BigDecimal amount) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전할 금액은 0보다 커야 합니다.");
        }

        if (amount.compareTo(MAX_CHARGE_AMOUNT) > 0) {
            throw new IllegalArgumentException("한 번에 충전할 수 있는 최대 금액은 20만원입니다.");
        }

        PointAccount account = pointAccountRepository.findByUserId(userId)
                .orElse(new PointAccount(null, userId, BigDecimal.ZERO));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.updateBalance(newBalance);
        pointAccountRepository.save(account);

        PaymentHistory history = new PaymentHistory(userId, amount, PointType.CHARGE, LocalDateTime.now());
        paymentHistoryRepository.save(history);

        return new UserBalanceResponse(
                new UserDto(user.getId(), user.getName(), user.getAddress(), user.getPhoneNumber(), user.getCreatedAt()),
                newBalance
        );
    }

    public List<PaymentHistory> getPaymentHistory(Long userId) {
        return paymentHistoryRepository.findByUserId(userId);
    }

    public AccountPointService(UsersRepository userRepository, PointAccountRepository pointAccountRepository, PaymentHistoryRepository paymentHistoryRepository) {
        this.userRepository = userRepository;
        this.pointAccountRepository = pointAccountRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

}
