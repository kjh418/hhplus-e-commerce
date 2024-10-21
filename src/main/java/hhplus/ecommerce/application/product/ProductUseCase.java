package hhplus.ecommerce.application.product;

import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.Top5ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductService productService;

    public List<ProductListDto> getProductList() {
        return productService.getProductList();
    }

    public ProductDetailDto getProductDetails(Long productId) {
        return productService.getProductDetails(productId);
    }

    public List<Top5ProductResponse> getTop5Products() {
        return productService.getTop5Products();
    }
}
