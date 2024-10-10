package hhplus.ecommerce.interfaces;

import hhplus.ecommerce.application.common.ErrorResponse;
import hhplus.ecommerce.application.common.OrderPaymentStatus;
import hhplus.ecommerce.application.order.OrderDetailRequest;
import hhplus.ecommerce.application.order.OrderDetailResponse;
import hhplus.ecommerce.application.order.OrderRequest;
import hhplus.ecommerce.application.order.OrderResponse;
import hhplus.ecommerce.application.product.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final BigDecimal MOCK_USER_POINT = new BigDecimal("100000");

    private final List<ProductDto> mockProducts = List.of(
            new ProductDto(100L, "긴팔티", new BigDecimal("20000"), 50, "초가을에 입기 좋은 옷", LocalDateTime.now()),
            new ProductDto(200L, "청바지", new BigDecimal("50000"), 0, "사계절 내내 입기 좋은 바지",LocalDateTime.now()),
            new ProductDto(300L, "후드티", new BigDecimal("70000"), 20, "따뜻하고 편안한 후드티", LocalDateTime.now()),
            new ProductDto(400L, "맨투맨", new BigDecimal("59000"), 15, "캐주얼하게 입기 좋은 맨투맨", LocalDateTime.now()),
            new ProductDto(500L, "슬랙스", new BigDecimal("49000"), 30, "편안한 슬랙스 바지", LocalDateTime.now())
    );

    @Operation(summary = "주문 및 결제 API", description = "상품을 주문 및 결제하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주문 및 결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/create")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequest orderRequest) {
        Long userId = orderRequest.getUserId();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetailResponse> orderDetails = new ArrayList<>();

        for (OrderDetailRequest detail : orderRequest.getOrderDetails()) {
            Long productId = detail.getProductId();
            int quantity = detail.getQuantity();
            BigDecimal productPrice = getProductPrice(productId);

            if (getAvailableStock(productId) < quantity) {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body(new ErrorResponse("해당 상품의 재고가 부족하여 주문이 불가능합니다."));
            }

            totalAmount = totalAmount.add(productPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        if (MOCK_USER_POINT.compareTo(totalAmount) < 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("포인트가 부족하여 결제가 불가능합니다."));
        }

        Long orderId = 1L;
        OrderPaymentStatus orderStatus = OrderPaymentStatus.PENDING;
        LocalDateTime createdAt = LocalDateTime.now();
        OrderPaymentStatus paymentStatus = OrderPaymentStatus.COMPLETED;
        LocalDateTime paymentDate = LocalDateTime.now();

        for (OrderDetailRequest detail : orderRequest.getOrderDetails()) {
            orderDetails.add(new OrderDetailResponse(detail.getProductId(), detail.getQuantity(), getProductPrice(detail.getProductId())));
        }

        OrderResponse response = new OrderResponse(orderId, userId, totalAmount, orderStatus, createdAt, orderDetails, totalAmount, paymentStatus, paymentDate);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private BigDecimal getProductPrice(Long productId) {
        return mockProducts.stream()
                .filter(product -> product.getProductId().equals(productId))
                .map(ProductDto::getPrice)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    private int getAvailableStock(Long productId) {
        return mockProducts.stream()
                .filter(product -> product.getProductId().equals(productId))
                .map(ProductDto::getStockQuantity)
                .findFirst()
                .orElse(0);
    }
}
