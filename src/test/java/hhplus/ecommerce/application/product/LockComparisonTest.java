package hhplus.ecommerce.application.product;

import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootTest
public class LockComparisonTest {

    @Autowired
    private ProductRepository productRepository;

    private final int THREAD_COUNT = 50;
    private final int TEST_REPEAT = 10;

    @DisplayName("낙관적 락과 비관적 락의 평균 성능 비교 테스트")
    @Test
    void compareLockPerformance() throws InterruptedException {
        long optimisticTotalTime = 0;
        long pessimisticTotalTime = 0;

        for (int i = 0; i < TEST_REPEAT; i++) {
            optimisticTotalTime += measureLockPerformance("낙관적");
            pessimisticTotalTime += measureLockPerformance("비관적");
        }

        System.out.printf("낙관적 락 평균 실행 시간: %dms\n", optimisticTotalTime / TEST_REPEAT);
        System.out.printf("비관적 락 평균 실행 시간: %dms\n", pessimisticTotalTime / TEST_REPEAT);
    }

    private long measureLockPerformance(String lockType) throws InterruptedException {
        Product product = productRepository.save(Product.builder()
                .name("청바지")
                .description("예쁜 청바지")
                .price(BigDecimal.valueOf(40000))
                .stockQuantity(100)
                .createdAt(LocalDateTime.now())
                .totalSales(0)
                .build());

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        long startMillis = System.currentTimeMillis();

        IntStream.range(0, THREAD_COUNT).forEach(i -> executorService.submit(() -> {
            try {
                if ("낙관적".equals(lockType)) {
                    productRepository.findByIdForOptimisticLock(product.getId()).ifPresent(p -> {
                        p.reduceStock(1);
                        productRepository.save(p);
                    });
                } else {
                    productRepository.findByIdForPessimisticLock(product.getId()).ifPresent(p -> {
                        p.reduceStock(1);
                        productRepository.save(p);
                    });
                }
            } finally {
                latch.countDown();
            }
        }));

        latch.await();
        executorService.shutdown();
        long duration = System.currentTimeMillis() - startMillis;
        System.out.printf("%s 락 실행 시간: %dms\n", lockType, duration);
        return duration;
    }
}
