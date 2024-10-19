package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.application.user.dto.UserDto;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PointAccount;
import hhplus.ecommerce.domain.payment.PointType;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.PaymentHistoryRepository;
import hhplus.ecommerce.infrastructure.repository.PointAccountRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountPointService {

    private static final BigDecimal MAX_CHARGE_AMOUNT = new BigDecimal("200000");

    private final UsersRepository userRepository;
    private final PointAccountRepository pointAccountRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    // 잔액 조회
    @Transactional
    public UserBalanceResponse getBalance(Long userId) {
        Users user = findUserById(userId);
        PointAccount account = findOrCreatePointAccount(userId);

        return createUserBalanceResponse(user, account);
    }

    // 포인트 충전
    @Transactional
    public UserBalanceResponse chargePoints(Long userId, BigDecimal amount) {
        Users user = findUserById(userId);
        validateChargeAmount(amount);

        PointAccount account = findOrCreatePointAccount(userId);
        BigDecimal newBalance = updateBalance(account, amount);
        account.updateBalance(newBalance);
        pointAccountRepository.save(account);

        savePaymentHistory(userId, amount);

        return new UserBalanceResponse(
                new UserDto(user.getId(), user.getName(), user.getAddress(), user.getPhoneNumber(), user.getCreatedAt()),
                newBalance
        );
    }

    // 포인트 이력 조회
    public List<PaymentHistory> getPaymentHistory(Long userId) {
        return paymentHistoryRepository.findByUserId(userId);
    }

    private Users findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));
    }

    private PointAccount findOrCreatePointAccount(Long userId) {
        return pointAccountRepository.findByUserId(userId)
                .orElseGet(() -> createPointAccount(userId));
    }

    private PointAccount createPointAccount(Long userId) {
        PointAccount newAccount = new PointAccount(null, userId, BigDecimal.ZERO);
        pointAccountRepository.save(newAccount);
        return newAccount;
    }

    private void validateChargeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전할 금액은 0보다 커야 합니다.");
        }
        if (amount.compareTo(MAX_CHARGE_AMOUNT) > 0) {
            throw new IllegalArgumentException("한 번에 충전할 수 있는 최대 금액은 20만원입니다.");
        }
    }

    // 잔액 업데이트
    private BigDecimal updateBalance(PointAccount account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().add(amount);
        account.updateBalance(newBalance);
        pointAccountRepository.save(account);
        return newBalance;
    }

    // 이력 저장
    private void savePaymentHistory(Long userId, BigDecimal amount) {
        PaymentHistory history = new PaymentHistory(userId, amount, PointType.CHARGE, LocalDateTime.now());
        paymentHistoryRepository.save(history);
    }

    private UserBalanceResponse createUserBalanceResponse(Users user, PointAccount account) {
        return new UserBalanceResponse(
                new UserDto(user.getId(), user.getName(), user.getAddress(), user.getPhoneNumber(), user.getCreatedAt()),
                account.getBalance()
        );
    }
}
