package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PointAccount;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.PaymentHistoryRepository;
import hhplus.ecommerce.infrastructure.repository.PointAccountRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountPointServiceTest {

    @InjectMocks
    private AccountPointService accountPointService;

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PointAccountRepository pointAccountRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    private Long userId;
    private Users user;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = new Users(userId, "홍길동", "서울시 강남구", "01012341234", LocalDateTime.now());
    }

    @Test
    void 포인트_이력이_없는_경우_잔액_0으로_초기화() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointAccountRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserBalanceResponse result = accountPointService.getBalance(userId);

        assertEquals(BigDecimal.ZERO, result.getBalance());
        verify(pointAccountRepository).save(any(PointAccount.class));
    }

    @Test
    void 포인트_잔액_조회_성공() {
        BigDecimal balance = new BigDecimal("10000");
        PointAccount pointAccount = new PointAccount(1L, userId, balance);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointAccountRepository.findByUserId(userId)).thenReturn(Optional.of(pointAccount));

        UserBalanceResponse result = accountPointService.getBalance(userId);

        assertEquals(userId, result.getUser().getUserId());
        assertEquals("홍길동", result.getUser().getName());
        assertEquals(balance, result.getBalance());
    }

    @Test
    void 포인트_충전_시_사용자가_존재하지_않을_때_예외_처리() {
        BigDecimal chargeAmount = new BigDecimal("5000");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            accountPointService.chargePoints(userId, chargeAmount);
        });

        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 포인트_충전_금액이_0원_이하일_때_예외_처리() {
        BigDecimal chargeAmount = new BigDecimal("-5000");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountPointService.chargePoints(userId, chargeAmount);
        });

        assertEquals("충전할 금액은 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    void 최대_충전_금액_초과_시_예외_처리() {
        BigDecimal chargeAmount = new BigDecimal("300000");
        PointAccount account = new PointAccount(1L, userId, new BigDecimal("10000"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountPointService.chargePoints(userId, chargeAmount);
        });

        assertEquals("한 번에 충전할 수 있는 최대 금액은 20만원입니다.", exception.getMessage());
    }

    @Test
    void 포인트_충전_성공() {
        BigDecimal chargeAmount = new BigDecimal("5000");

        Users user = new Users(userId, "홍길동", "서울시 강남구", "01012341234", LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointAccountRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserBalanceResponse result = accountPointService.chargePoints(userId, chargeAmount);

        assertEquals(chargeAmount, result.getBalance());
        verify(pointAccountRepository).save(any(PointAccount.class));
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }
}