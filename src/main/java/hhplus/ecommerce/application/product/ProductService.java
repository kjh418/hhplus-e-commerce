package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.common.ErrorCode;
import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.TopProductResponse;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

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
        LocalDateTime endDate = LocalDateTime.now(); // 현재 날짜
        LocalDateTime startDate = endDate.minusDays(days);

        List<TopProductResponse> topProducts = productRepository.findTopBySales(startDate, endDate);

        return topProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
