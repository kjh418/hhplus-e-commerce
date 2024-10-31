package hhplus.ecommerce.infrastructure.filter;


import hhplus.ecommerce.application.common.ErrorCode;
import hhplus.ecommerce.application.payment.AccountPointService;
import hhplus.ecommerce.application.payment.dto.PaymentDto;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DuplicateRequestFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountPointService accountPointService;

    private Long testUserId;
    private Long testOrderId;

    @BeforeEach
    public void setUp() {
        Users testUser = new Users(null, "홍길동", "서울특별시", "010-1234-5678", LocalDateTime.now());
        testUserId = usersRepository.save(testUser).getId();

        accountPointService.chargePoints(testUserId, new PaymentDto(new BigDecimal("500.00")), null);

        Orders testOrder = new Orders(testUserId, new BigDecimal("100.00"), OrderStatus.PENDING, LocalDateTime.now());
        testOrderId = ordersRepository.save(testOrder).getId();
    }

    @Test
    @Transactional
    @Rollback
    public void 중복_결제_요청이_들어왔을_때_차단_테스트() throws Exception {
        mockMvc.perform(post("/payment/" + testOrderId)
                        .param("userId", testUserId.toString())
                        .param("paymentAmount", "100.00"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/payment/" + testOrderId)
                        .param("userId", testUserId.toString())
                        .param("paymentAmount", "100.00"))
                .andExpect(status().isConflict())  // 중복 요청 시 409 응답을 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // 응답이 JSON 형식인지 확인
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_REQUEST.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.DUPLICATE_REQUEST.getCode()));
    }
}