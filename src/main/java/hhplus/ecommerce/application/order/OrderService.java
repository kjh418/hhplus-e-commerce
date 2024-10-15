package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.common.OrderPaymentStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.domain.user.Users;
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

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 사용자 정보 조회
        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 주문 총액 계산 및 상품 재고 확인
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

        Orders orders = new Orders(user.getId(), totalAmount, OrderPaymentStatus.PENDING, LocalDateTime.now());
        orderRepository.save(orders);

        for (OrderDetailRequest detailRequest : request.getOrderDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

            // 재고 확인 및 감소
            product.reduceStock(detailRequest.getQuantity());

            // 주문 상세 정보 추가
            BigDecimal productTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(detailRequest.getQuantity()));
            totalAmount = totalAmount.add(productTotalPrice);

            // 주문 상세 내역 저장
            OrderDetailResponse detailResponse = new OrderDetailResponse(
                    product.getId(), detailRequest.getQuantity(), product.getPrice()
            );
            orderDetailResponses.add(detailResponse);

            productRepository.save(product);
        }

        return new OrderResponse(
                null,
                user.getId(),
                totalAmount,
                OrderPaymentStatus.PENDING,
                LocalDateTime.now(),
                orderDetailResponses,
                totalAmount,
                OrderPaymentStatus.PENDING,
                LocalDateTime.now(),
                null
        );
    }
}