package hhplus.ecommerce.application.product;

import com.jayway.jsonpath.JsonPath;
import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.TopProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest {
    @Autowired
    private ProductUseCase productUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Test
    void 상품_목록_조회() throws Exception {
        // 상품 목록을 조회하는 요청을 보냄
        MvcResult result = mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("청바지"))
                .andExpect(jsonPath("$[1].name").value("후드티"))
                .andReturn();

        // JSON 응답 파싱 및 검증
        String jsonResponse = result.getResponse().getContentAsString();
        List<ProductListDto> productList = JsonPath.parse(jsonResponse).read("$", List.class);

        assertNotNull(productList);
        assertEquals(2, productList.size());

        // 필드 값 확인을 위해 객체를 직접 생성하여 검증
        ProductListDto firstProduct = productList.get(0);
        ProductListDto secondProduct = productList.get(1);
        assertEquals("청바지", firstProduct.getName());
        assertEquals("티셔츠", secondProduct.getName());
    }

    @Test
    void 상품_상세_정보_조회_테스트() {
        ProductDetailDto productDetail = productUseCase.getProductDetails(1L);

        assertNotNull(productDetail);
        assertEquals("청바지", productDetail.getName());
        assertEquals(50, productDetail.getStockQuantity());
    }

    @Test
    void 싱위_상품_조회() {
        List<TopProductResponse> topProducts = productUseCase.getTopProducts(3, 5);

        assertNotNull(topProducts);
        assertEquals(2, topProducts.size());
        assertEquals("청바지", topProducts.get(0).getName());
        assertEquals(100, topProducts.get(0).getTotalSales());
    }

    @Test
    void 사용자_10명이_동시에_동일한_상품을_구매할_경우_재고_감소_테스트() throws Exception {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        int purchaseQuantity = 1;
        Long productId = 1L;

        // 현재 재고 수량
        MvcResult result = mockMvc.perform(get("/products/{productId}", productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        if (jsonResponse.isEmpty()) {
            throw new IllegalStateException("상품 ID가 유효하지 않거나, 잘못된 경로일 수 있습니다. "
                    + "상품 ID: " + productId + ", 응답 코드: " + result.getResponse().getStatus());
        }

        int currentStockQuantity = JsonPath.parse(jsonResponse).read("$.stockQuantity", Integer.class);

        // 예상 재고 수량 계산
        int expectedStockQuantity = currentStockQuantity - (numberOfThreads * purchaseQuantity);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(post("/products/{productId}/purchase", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"quantity\": \"" + purchaseQuantity + "\"}"))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(expectedStockQuantity));
    }
}