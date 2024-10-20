package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.product.ProductService;
import hhplus.ecommerce.application.product.dto.ProductDetailDto;
import hhplus.ecommerce.application.product.dto.ProductListDto;
import hhplus.ecommerce.application.product.dto.Top5ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductListDto>> getAllProducts() {
        List<ProductListDto> productList = productService.getProductList();
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getProductDetail(@PathVariable Long id) {
        ProductDetailDto product = productService.getProductDetails(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/top5")
    public ResponseEntity<List<Top5ProductResponse>> getTop5Products() {
        List<Top5ProductResponse> topProducts = productService.getTop5Products();
        return ResponseEntity.ok(topProducts);
    }
}
