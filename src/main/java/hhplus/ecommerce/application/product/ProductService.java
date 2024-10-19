package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.Top5ProductResponse;
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

    public List<Top5ProductResponse> getTop5Products() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<Product> topProducts = productRepository.findTop5BySales(threeDaysAgo);

        return topProducts.stream()
                .map(product -> new Top5ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getTotalSales()
                ))
                .collect(Collectors.toList());
    }

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}
