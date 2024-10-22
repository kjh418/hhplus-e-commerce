package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.TopProductResponse;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductListDto> getProductList() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> new ProductListDto(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());
    }

    public ProductDetailDto getProductDetails(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        return new ProductDetailDto(product.getId(), product.getName(), product.getPrice(), product.getStockQuantity(), product.getDescription());
    }

    public List<TopProductResponse> getTopProducts(int days, int limit) {
        LocalDateTime endDate = LocalDateTime.now(); // 현재 날짜
        LocalDateTime startDate = endDate.minusDays(days);

        List<TopProductResponse> topProducts = productRepository.findTopBySales(startDate, endDate);

        return topProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
