package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.order.dto.OrderDetailRequest;
import hhplus.ecommerce.application.order.dto.OrderRequest;
import hhplus.ecommerce.application.order.dto.OrderResponse;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrderUseCaseIntegrationTest {

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    private Long userId;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        product1 = Product.builder()
                .name("청바지")
                .description("색상이 예쁜 청바지")
                .price(new BigDecimal("40000"))
                .stockQuantity(100)
                .createdAt(LocalDateTime.now())
                .totalSales(0)
                .build();

        product2 = Product.builder()
                .name("긴팔티")
                .description("환절기에 딱인 긴팔티")
                .price(new BigDecimal("20000"))
                .stockQuantity(50)
                .createdAt(LocalDateTime.now())
                .totalSales(0)
                .build();

        // 상품을 데이터베이스에 저장
        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);

        // 기존 주문 데이터 삭제
        ordersRepository.deleteAll();
    }

    @Test
    public void 주문_생성_통합_테스트() {
        List<OrderDetailRequest> orderDetails = List.of(
                new OrderDetailRequest(product1.getId(), 2),
                new OrderDetailRequest(product2.getId(), 1)
        );

        OrderRequest orderRequest = new OrderRequest(userId, orderDetails);

        OrderResponse orderResponse = orderUseCase.createOrder(orderRequest);

        assertNotNull(orderResponse);
        assertEquals(userId, orderResponse.getUserId());
        assertThat(orderResponse.getOrderDetails().size()).isEqualTo(2);
        assertThat(orderResponse.getTotalAmount()).isGreaterThan(BigDecimal.ZERO);

        Product updatedProduct1 = productRepository.findById(product1.getId()).orElse(null);
        assertNotNull(updatedProduct1);
        assertThat(updatedProduct1.getStockQuantity()).isEqualTo(98);

        Product updatedProduct2 = productRepository.findById(product2.getId()).orElse(null);
        assertNotNull(updatedProduct2);
        assertThat(updatedProduct2.getStockQuantity()).isEqualTo(49);
    }

    @Test
    public void 재고가_10개_남아_있을_떄_20명의_이용자가_동시에_구매시도_시_10명만_성공() throws InterruptedException {
        List<OrderDetailRequest> orderDetails = List.of(
                new OrderDetailRequest(product1.getId(), 1)
        );

        OrderRequest orderRequest = new OrderRequest(userId, orderDetails);

        int numberOfUsers = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);

        List<String> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                try {
                    OrderResponse response = orderUseCase.createOrder(orderRequest);
                    results.add("구매 성공");
                } catch (Exception e) {
                    results.add("구매 실패: 재고 부족");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long successCount = results.stream().filter(result -> result.equals("구매 성공")).count();
        long failureCount = results.stream().filter(result -> result.equals("구매 실패: 재고 부족")).count();

        assertEquals(10, successCount);
        assertEquals(10, failureCount);

        Product updatedProduct = productRepository.findById(product1.getId()).orElse(null);
        assertNotNull(updatedProduct);
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(0);
    }
}