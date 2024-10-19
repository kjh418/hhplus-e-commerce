package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.product.ProductDetailDto;
import hhplus.ecommerce.application.product.ProductListDto;
import hhplus.ecommerce.application.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void 상품_목록_조회() throws Exception {
        List<ProductListDto> products = List.of(
                new ProductListDto(1L, "청바지", new BigDecimal("50000")),
                new ProductListDto(2L, "맨투맨", new BigDecimal("39000"))
        );
        when(productService.getProductList()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2)) // 반환된 리스트 크기 확인
                .andExpect(jsonPath("$[0].name").value("청바지")); // 첫 번째 상품 name 확인
    }

    @Test
    public void 상품_상세_조회() throws Exception {
        ProductDetailDto productDto = new ProductDetailDto(1L, "청바지", new BigDecimal("50000"), 10, "사계절 내내 입기 좋은 청바지");
        when(productService.getProductDetails(1L)).thenReturn(productDto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("청바지"));
    }
}