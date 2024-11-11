package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.common.ErrorCode;
import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.TopProductResponse;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    @Cacheable(value = "productListCache", key = "'allProducts'", unless = "#result.isEmpty()")
    public List<ProductListDto> getProductList() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> new ProductListDto(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "productDetailsCache", key = "#productId")
    public ProductDetailDto getProductDetails(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));

        return new ProductDetailDto(product.getId(), product.getName(), product.getPrice(), product.getStockQuantity(), product.getDescription());
    }

    @Cacheable(value = "topProductsCache", key = "#days + '-' + #limit", unless = "#result.isEmpty()")
    public List<TopProductResponse> getTopProducts(int days, int limit) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<TopProductResponse> topProducts = productRepository.findTopBySales(startDate, endDate);

        return topProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 12 * 60 * 60 * 1000) // 12시간마다 주기적으로 인기 상품 캐시 갱신
    public void evictTopProductsCache() {
        logger.info("인기 상품 캐시가 무효화되었습니다.");
    }
}
