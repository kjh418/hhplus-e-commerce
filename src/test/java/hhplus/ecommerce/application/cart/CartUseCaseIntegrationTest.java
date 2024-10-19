package hhplus.ecommerce.application.cart;

import hhplus.ecommerce.domain.cart.CartItem;
import hhplus.ecommerce.infrastructure.repository.CartItemRepository;
import hhplus.ecommerce.infrastructure.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class CartUseCaseIntegrationTest {

    @Autowired
    private CartUseCase cartUseCase;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long userId;
    private Long productId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        productId = 1L;
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    public void 장바구니에_아이템_추가_통합_테스트() {
        int quantity = 2;
        boolean isSelected = true;

        List<CartItem> cartItems = cartUseCase.addItemAndReturnCartItems(userId, productId, quantity, isSelected);

        assertNotNull(cartItems);
        assertEquals(1, cartItems.size());
        assertEquals(quantity, cartItems.get(0).getQuantity());
    }

    @Test
    public void 장바구니에서_아이템_삭제_통합_테스트() {
        int quantity = 2;
        boolean isSelected = true;
        List<CartItem> cartItems = cartUseCase.addItemAndReturnCartItems(userId, productId, quantity, isSelected);
        Long cartItemId = cartItems.get(0).getId();

        cartItems = cartUseCase.removeItemAndReturnCartItems(userId, cartItemId);

        assertNotNull(cartItems);
        assertEquals(0, cartItems.size());
    }

    @Test
    public void 장바구니_아이템_조회_통합_테스트() {
        int quantity = 2;
        boolean isSelected = true;
        cartUseCase.addItemAndReturnCartItems(userId, productId, quantity, isSelected);

        List<CartItem> cartItems = cartUseCase.getCartItems(userId);

        assertNotNull(cartItems);
        assertEquals(1, cartItems.size());
        assertEquals(productId, cartItems.get(0).getProductId());
    }
}
