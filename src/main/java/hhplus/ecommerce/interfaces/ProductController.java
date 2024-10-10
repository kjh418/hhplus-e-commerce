package hhplus.ecommerce.interfaces;

import hhplus.ecommerce.application.product.PopularProductDto;
import hhplus.ecommerce.application.product.PopularProductResponse;
import hhplus.ecommerce.application.product.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<ProductDto> mockProducts = List.of(
            new ProductDto(100L, "긴팔티", new BigDecimal("20000"), 50, "초가을에 입기 좋은 옷", LocalDateTime.now()),
            new ProductDto(200L, "청바지", new BigDecimal("50000"), 10, "사계절 내내 입기 좋은 바지",LocalDateTime.now()),
            new ProductDto(300L, "후드티", new BigDecimal("70000"), 20, "따뜻하고 편안한 후드티", LocalDateTime.now()),
            new ProductDto(400L, "맨투맨", new BigDecimal("59000"), 15, "캐주얼하게 입기 좋은 맨투맨", LocalDateTime.now()),
            new ProductDto(500L, "슬랙스", new BigDecimal("49000"), 30, "편안한 슬랙스 바지", LocalDateTime.now())
    );

    private final List<PopularProductDto> popularProducts = List.of(
            new PopularProductDto(1L, 100L, 500L, 500L, new BigDecimal("4.5"), 120L, LocalDateTime.parse("2023-10-01 10:30:45", formatter)),
            new PopularProductDto(2L, 200L, 300L, 300L, new BigDecimal("4.0"), 150L, LocalDateTime.parse("2023-10-02 11:15:30", formatter)),
            new PopularProductDto(3L, 300L, 450L, 450L, new BigDecimal("4.8"), 90L, LocalDateTime.parse("2023-10-03 09:45:20", formatter)),
            new PopularProductDto(4L, 400L, 200L, 200L, new BigDecimal("3.9"), 180L, LocalDateTime.parse("2023-10-04 14:20:10", formatter)),
            new PopularProductDto(5L, 500L, 350L, 350L, new BigDecimal("4.7"), 110L, LocalDateTime.parse("2023-10-05 16:10:05", formatter))
    );

    @Operation(summary="상품 목록 조회 API", description = "모든 상품 목록 조회")
    @GetMapping
    public ResponseEntity<List<ProductDto>> findAll() {
        return ResponseEntity.ok(mockProducts);
    }

    @Operation(summary = "상품 상세 조회 API", description = "상품 ID로 해당 상품의 상세 정보 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상품 상세정보 조회됨"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> findById(@PathVariable Long productId) {
        return mockProducts.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "인기 상품 목록 조회 API", description = "조회수, 평점, 주문 수에 따라 정렬된 상위 상품 목록 조회")
    @ApiResponse(responseCode = "200", description = "인기 상품 목록 조회됨")
    @GetMapping("/popular")
    public ResponseEntity<List<PopularProductResponse>> findPopularProducts(@RequestParam(defaultValue = "view_count") String sortBy) {
        List<PopularProductResponse> result = popularProducts.stream()
                .sorted(getComparator(sortBy))
                .map(popularProduct -> {
                            ProductDto product = mockProducts.stream()
                                    .filter(p-> p.getProductId().equals((popularProduct.getProductId())))
                                    .findFirst()
                                    .orElse(null);

                            if(product != null){
                                return new PopularProductResponse(
                                        popularProduct.getProductId(),
                                        product.getName(),
                                        product.getPrice(),
                                        popularProduct.getTotalSales(),
                                        popularProduct.getViewCount(),
                                        popularProduct.getAvgRating(),
                                        popularProduct.getOrderCount()
                                );
                            }
                            return null;
                        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private Comparator<PopularProductDto> getComparator(String sortBy) {
        switch (sortBy) {
            case "avg_rating":
                return Comparator.comparing(PopularProductDto::getAvgRating).reversed();
            case "order_count":
                return Comparator.comparing(PopularProductDto::getOrderCount).reversed();
            default:
                return Comparator.comparing(PopularProductDto::getViewCount).reversed();
        }
    }
}
