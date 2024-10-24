package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.TopProductResponse;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "청바지", "사계절 내내 입을 수 있는 청바지", new BigDecimal("50000"), 10, LocalDateTime.now(), 15);
        product2 = new Product(2L, "맨투맨", "편안한 맨투맨", new BigDecimal("39000"), 15, LocalDateTime.now(), 20);

        productService = new ProductService(productRepository);
    }

    @Test
    void 상품_목록_조회() {

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<ProductListDto> productList = productService.getProductList();

        assertThat(productList).isNotNull();
        assertThat(productList.size()).isEqualTo(2);
        assertThat(productList.get(0).getName()).isEqualTo("청바지");
        assertThat(productList.get(1).getName()).isEqualTo("맨투맨");
    }

    @Test
    void 존재하지_않는_상품_상세_조회_시_예외_발생() {
        Long invalidProductId = 999L;
        when(productRepository.findById(invalidProductId)).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.getProductDetails(invalidProductId);
        });

        assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 존재하는_상품_상세_조회() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        ProductDetailDto productDetail = productService.getProductDetails(1L);

        assertThat(productDetail).isNotNull();
        assertThat(productDetail.getProductId()).isEqualTo(1L);
        assertThat(productDetail.getName()).isEqualTo("청바지");
        assertThat(productDetail.getPrice()).isEqualTo(new BigDecimal("50000"));
        assertThat(productDetail.getStockQuantity()).isEqualTo(10);
        assertThat(productDetail.getDescription()).isEqualTo("사계절 내내 입을 수 있는 청바지");
    }

    @Test
    void 상위_5개_상품_조회_성공() {
        LocalDateTime.now().minusDays(3);

        Product product1 = new Product(1L, "청바지", "사계절 내내 입을 수 있는 청바지", new BigDecimal("50000"), 10, LocalDateTime.now(), 15);
        Product product2 = new Product(2L, "맨투맨", "편안한 맨투맨", new BigDecimal("39000"), 15, LocalDateTime.now(), 20);

        when(productRepository.findTopBySales(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(new TopProductResponse(product1.getId(), product1.getName(), product1.getPrice(), product1.getTotalSales()),
                        new TopProductResponse(product2.getId(), product2.getName(), product2.getPrice(), product2.getTotalSales())));

        List<TopProductResponse> result = productService.getTopProducts(3, 5);

        assertEquals(2, result.size());
        assertEquals("청바지", result.get(0).getName());
        assertEquals(15, result.get(0).getTotalSales());
    }

}