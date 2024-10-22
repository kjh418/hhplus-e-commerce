package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.order.dto.OrderDetailRequest;
import hhplus.ecommerce.application.order.dto.OrderDetailResponse;
import hhplus.ecommerce.application.order.dto.OrderRequest;
import hhplus.ecommerce.application.order.dto.OrderResponse;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.order.OrdersDetail;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.OrdersDetailRepository;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final OrdersRepository orderRepository;
    private final OrdersDetailRepository ordersDetailRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 사용자 정보 조회
        Users user = findUserById(request.getUserId());

        // 주문 생성 및 상세 정보 처리
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        BigDecimal totalAmount = processOrderDetails(request.getOrderDetails(), orderDetailResponses);

        Orders orders = saveOrder(user.getId(), totalAmount);

        saveOrderDetails(orders, request.getOrderDetails());

        return buildOrderResponse(orders, orderDetailResponses);
    }

    private Users findUserById(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));
    }

    private BigDecimal processOrderDetails(List<OrderDetailRequest> orderDetails, List<OrderDetailResponse> orderDetailResponses) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDetailRequest detailRequest : orderDetails) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

            reduceStock(product, detailRequest.getQuantity());

            BigDecimal productTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantity()));
            totalAmount = totalAmount.add(productTotalPrice);

            OrderDetailResponse detailResponse = new OrderDetailResponse(
                    product.getId(), detailRequest.getQuantity(), product.getPrice()
            );
            orderDetailResponses.add(detailResponse);
        }

        return totalAmount;
    }

    private void reduceStock(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        product.reduceStock(quantity);
        productRepository.save(product);
    }

    private Orders saveOrder(Long userId, BigDecimal totalAmount) {
        Orders orders = new Orders(userId, totalAmount, OrderStatus.PENDING, LocalDateTime.now());
        return orderRepository.save(orders);
    }

    private void saveOrderDetails(Orders orders, List<OrderDetailRequest> orderDetails) {
        for (OrderDetailRequest detailRequest : orderDetails) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

            OrdersDetail ordersDetail = new OrdersDetail(
                    orders.getId(),
                    product.getId(),
                    detailRequest.getQuantity(),
                    product.getPrice()
            );

            ordersDetailRepository.save(ordersDetail);
        }
    }

    private OrderResponse buildOrderResponse(Orders orders, List<OrderDetailResponse> orderDetailResponses) {
        return new OrderResponse(
                orders.getId(),
                orders.getUserId(),
                orders.getTotalAmount(),
                orders.getStatus(),
                orders.getCreatedAt(),
                orderDetailResponses,
                orders.getTotalAmount(),
                PaymentStatus.PENDING,
                LocalDateTime.now(),
                null
        );
    }
}