package hhplus.ecommerce.application.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
}