package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.TopProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductIntegrationTest {
    @Autowired
    private ProductUseCase productUseCase;

    @MockBean
    private ProductService productService;

    @Test
    public void testGetProductList() {
        List<ProductListDto> mockProductList = List.of(
                new ProductListDto(1L, "청바지", new BigDecimal("10000")),
                new ProductListDto(2L, "티셔츠", new BigDecimal("20000"))
        );
        when(productService.getProductList()).thenReturn(mockProductList);

        List<ProductListDto> productList = productUseCase.getProductList();

        assertNotNull(productList);
        assertEquals(2, productList.size());
        assertEquals("청바지", productList.get(0).getName());
    }

    @Test
    public void testGetProductDetails() {
        ProductDetailDto mockProduct = new ProductDetailDto(1L, "청바지", new BigDecimal("10000"), 50, "Description");
        when(productService.getProductDetails(1L)).thenReturn(mockProduct);

        ProductDetailDto productDetail = productUseCase.getProductDetails(1L);

        assertNotNull(productDetail);
        assertEquals("청바지", productDetail.getName());
        assertEquals(50, productDetail.getStockQuantity());
    }

    @Test
    public void testGetTopProducts() {
        List<TopProductResponse> mockTopProducts = List.of(
                new TopProductResponse(1L, "청바지", new BigDecimal("10000"), 100),
                new TopProductResponse(2L, "티셔츠", new BigDecimal("20000"), 80)
        );
        when(productService.getTopProducts(anyInt(), anyInt())).thenReturn(mockTopProducts);

        List<TopProductResponse> topProducts = productUseCase.getTopProducts(3, 5);

        assertNotNull(topProducts);
        assertEquals(2, topProducts.size());
        assertEquals("청바지", topProducts.get(0).getName());
        assertEquals(100, topProducts.get(0).getTotalSales());
    }
}