package hhplus.ecommerce.application.payment;

import hhplus.ecommerce.application.payment.dto.PaymentDto;
import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.application.user.dto.UserDto;
import hhplus.ecommerce.domain.payment.PaymentHistory;
import hhplus.ecommerce.domain.payment.PointType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountPointUseCaseTest {

    @Mock
    private AccountPointService accountPointService;

    @InjectMocks
    private AccountPointUseCase accountPointUseCase;

    private Long userId;
    private PaymentDto paymentDto;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        paymentDto = new PaymentDto(new BigDecimal("50000"));
    }

    @Test
    public void 포인트_잔액_조회_테스트() {
        UserBalanceResponse expectedResponse = new UserBalanceResponse(
                new UserDto(userId, "홍길동", "서울", "010-1234-5678", LocalDateTime.now()),
                new BigDecimal("150000")
        );
        when(accountPointService.getBalance(userId)).thenReturn(expectedResponse);

        UserBalanceResponse result = accountPointUseCase.getBalance(userId);

        assertNotNull(result);
        assertEquals(new BigDecimal("150000"), result.getBalance());
        verify(accountPointService, times(1)).getBalance(userId);
    }

    @Test
    public void 포인트_충전_테스트() {
        UserBalanceResponse expectedResponse = new UserBalanceResponse(
                new UserDto(userId, "홍길동", "서울", "010-1234-5678", LocalDateTime.now()),
                new BigDecimal("200000")
        );
        when(accountPointService.chargePoints(userId, paymentDto, null)).thenReturn(expectedResponse);

        UserBalanceResponse result = accountPointUseCase.chargePoints(userId, paymentDto, null);

        assertNotNull(result);
        assertEquals(new BigDecimal("200000"), result.getBalance());
        verify(accountPointService, times(1)).chargePoints(userId, paymentDto, null);
    }

    @Test
    public void 포인트_이력_조회_테스트() {
        List<PaymentHistory> expectedHistory = List.of(
                new PaymentHistory(userId, new BigDecimal("50000"), PointType.CHARGE, LocalDateTime.now()),
                new PaymentHistory(userId, new BigDecimal("30000"), PointType.CHARGE, LocalDateTime.now())
        );
        when(accountPointService.getPaymentHistory(userId)).thenReturn(expectedHistory);

        List<PaymentHistory> result = accountPointUseCase.getPaymentHistory(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountPointService, times(1)).getPaymentHistory(userId);
    }
}