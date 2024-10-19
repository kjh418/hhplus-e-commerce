package hhplus.ecommerce.application.order;

import hhplus.ecommerce.application.order.dro.OrderDetailRequest;
import hhplus.ecommerce.application.order.dro.OrderRequest;
import hhplus.ecommerce.application.order.dro.OrderResponse;
import hhplus.ecommerce.application.user.dto.UserBalanceResponse;
import hhplus.ecommerce.application.user.dto.UserDto;
import hhplus.ecommerce.domain.order.OrderStatus;
import hhplus.ecommerce.domain.order.Orders;
import hhplus.ecommerce.domain.payment.PaymentStatus;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.domain.user.Users;
import hhplus.ecommerce.infrastructure.repository.OrdersRepository;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import hhplus.ecommerce.infrastructure.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void 존재하지_않는_상품을_주문하는_경우_예외_발생() {
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderDetailRequest(999L, 2)
        ));

        Users user = new Users(1L, "홍길동", "서울시 강남구", "01012341234", LocalDateTime.now());
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(request);
        });

        assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 존재하지_않는_사용자로_주문할_경우_예외_발생() {
        OrderRequest request = new OrderRequest(999L, List.of(
                new OrderDetailRequest(1L, 2)
        ));

        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(request);
        });

        assertEquals("사용자가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 재고가_없는_상품을_주문하려고_하는_경우_예외_발생() {
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderDetailRequest(1L, 2)
        ));

        Product product = new Product(1L, "티셔츠", "가을에 입기 좋은 티셔츠", new BigDecimal("10000"), 1, LocalDateTime.now(), 10);

        Users user = new Users(1L, "홍길동", "서울시 강남구", "01012341234", LocalDateTime.now());
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(request);
        });

        assertEquals("재고가 부족합니다.", exception.getMessage());
    }

    @Test
    void 주문_생성_결제_직전까지_성공하는_경우() {
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderDetailRequest(1L, 2)
        ));

        Product product = new Product(1L, "티셔츠", "가을에 입기 좋은 티셔츠", new BigDecimal("10000"), 10, LocalDateTime.now(), 10);
        Users user = new Users(1L, "홍길동", "서울시 강남구", "01012341234", LocalDateTime.now());

        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getAddress(), user.getPhoneNumber(), user.getCreatedAt());

        UserBalanceResponse userBalance = new UserBalanceResponse(userDto, new BigDecimal("50000"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        Orders order = new Orders(1L, user.getId(), new BigDecimal("20000"), OrderStatus.PENDING, LocalDateTime.now());
        when(ordersRepository.save(any(Orders.class))).thenReturn(order);

        OrderResponse orderResponse = orderService.createOrder(request);

        assertNotNull(orderResponse);
        assertEquals(PaymentStatus.PENDING, orderResponse.getPaymentStatus());
        assertEquals(new BigDecimal("20000"), orderResponse.getTotalAmount());
        assertEquals(new BigDecimal("50000"), userBalance.getBalance());
        assertEquals(user.getId(), userBalance.getUser().getUserId());
    }
}
