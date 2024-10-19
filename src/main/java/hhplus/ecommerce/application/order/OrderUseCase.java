package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.order.dto.OrderRequest;
import hhplus.ecommerce.application.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderUseCase {

    private final OrderService orderService;

    public OrderResponse createOrder(OrderRequest request) {
        return orderService.createOrder(request);
    }
}