package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.common.ErrorResponse;
import hhplus.ecommerce.application.order.OrderService;
import hhplus.ecommerce.application.order.dto.OrderRequest;
import hhplus.ecommerce.application.order.dto.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderResponse orderResponse = orderService.createOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(orderResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("사용자나 상품이 존재하지 않습니다."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("서버 오류가 발생했습니다."));
        }
    }
}
