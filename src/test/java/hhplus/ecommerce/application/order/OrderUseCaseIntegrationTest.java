package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.order.dto.OrderDetailRequest;
import hhplus.ecommerce.application.order.dto.OrderRequest;
import hhplus.ecommerce.application.order.dto.OrderResponse;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class OrderUseCaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderUseCaseIntegrationTest.class);

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Long userId;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        Users user = Users.builder()
                .name("테스트 사용자")
                .address("서울시 강남구")
                .phoneNumber("010-1234-5678")
                .createdAt(LocalDateTime.now())
                .build();

        user = usersRepository.save(user);
        userId = user.getId();

        product1 = Product.builder()
                .name("청바지")
                .description("색상이 예쁜 청바지")
                .price(new BigDecimal("40000"))
                .stockQuantity(10)
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
    public void 재고가_10개_남아_있을_때_20명의_이용자가_동시에_구매_시도_시_10명만_성공() throws InterruptedException {

        int numberOfUsers = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);

        // 스레드별 성공 및 실패 개수 카운트
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfUsers; i++) {
            executorService.submit(() -> {
                try {
                    successCount.incrementAndGet();
                    logger.info("구매 성공 - Thread ID: {}", Thread.currentThread().getId());
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    logger.warn("구매 실패 - Thread ID: {}, 이유: {}", Thread.currentThread().getId(), e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertEquals(10, successCount.get());
        assertEquals(10, failureCount.get());

        Product updatedProduct = productRepository.findById(product1.getId()).orElse(null);
        assertNotNull(updatedProduct);
        assertEquals(0, updatedProduct.getStockQuantity());
    }
}