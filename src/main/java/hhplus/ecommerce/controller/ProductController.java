package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.product.ProductDetailDto;
import hhplus.ecommerce.application.product.ProductListDto;
import hhplus.ecommerce.application.product.ProductService;
import hhplus.ecommerce.application.product.Top5ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
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

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
}
