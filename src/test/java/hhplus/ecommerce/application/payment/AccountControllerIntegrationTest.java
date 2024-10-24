package hhplus.ecommerce.application.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("잔액 충전 성공")
    @Test
    void 잔액_충전_성공() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/points/{userId}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
                        .content("{\"amount\": 10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10000));
    }

    @DisplayName("잔액 조회 성공")
    @Test
    void 잔액_조회_성공() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/points/{userId}/balance", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10000));
    }

    @Test
    void 사용자_10명이_동시에_포인트_1000씩_충전할_경우_순서대로_처리_테스트() throws Exception {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        BigDecimal chargeAmount = new BigDecimal("1000");

        for (int i = 0; i < numberOfThreads; i++) {
            final Long userId = (long) (i + 1);

            executorService.submit(() -> {
                try {
                    mockMvc.perform(post("/points/{userId}/charge", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content("{\"amount\": \"" + chargeAmount + "\"}"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.balance").value(chargeAmount.intValue()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        for (int i = 0; i < numberOfThreads; i++) {
            Long userId = (long) (i + 1);
            mockMvc.perform(get("/points/{userId}/balance", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(chargeAmount.intValue()));
        }
    }
}