package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.common.ErrorCode;
import hhplus.ecommerce.application.payment.dto.PaymentDto;
import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.application.user.dto.UserDto;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PointAccount;
import hhplus.ecommerce.domain.payment.PointType;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.PaymentHistoryRepository;
import hhplus.ecommerce.infrastructure.repository.PointAccountRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import jakarta.validation.Valid;
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

    @Transactional
    public UserBalanceResponse chargePoints(Long userId, @Valid PaymentDto paymentDto, Long orderId) {
        Users user = findUserById(userId);
        validateChargeAmount(paymentDto.getAmount());

        PointAccount account = findOrCreatePointAccount(userId);
        BigDecimal newBalance = updateBalance(account, paymentDto.getAmount());
        account.updateBalance(newBalance);
        pointAccountRepository.save(account);

        savePaymentHistory(userId, orderId, paymentDto.getAmount());

        return createUserBalanceResponse(user, account);
    }

    // 포인트 이력 조회
    public List<PaymentHistory> getPaymentHistory(Long userId) {
        return paymentHistoryRepository.findByUserId(userId);
    }

    private Users findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));
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
            throw new IllegalArgumentException(ErrorCode.NEGATIVE_POINT_AMOUNT.getMessage());
        }
        if (amount.compareTo(MAX_CHARGE_AMOUNT) > 0) {
            throw new IllegalArgumentException(ErrorCode.MAXIMUM_CHARGE_LIMIT_EXCEEDED.getMessage());
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
    private void savePaymentHistory(Long userId, Long orderId, BigDecimal amount) {
        PaymentHistory history = new PaymentHistory(userId, orderId, amount, PointType.CHARGE, LocalDateTime.now());
        paymentHistoryRepository.save(history);
    }

    private UserBalanceResponse createUserBalanceResponse(Users user, PointAccount account) {
        return new UserBalanceResponse(
                new UserDto(user.getId(), user.getName(), user.getAddress(), user.getPhoneNumber(), user.getCreatedAt()),
                account.getBalance()
        );
    }
}
