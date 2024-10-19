package hhplus.ecommerce.application.cart;

import hhplus.ecommerce.domain.cart.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartUseCaseTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartUseCase cartUseCase;

    private Long userId;
    private Long productId;
    private Long cartItemId;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        productId = 1L;
        cartItemId = 1L;
    }

    @Test
    public void 장바구니에_아이템_추가_테스트() {
        CartItem cartItem = new CartItem(1L, productId, 2, true, LocalDateTime.now());
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        doNothing().when(cartService).addItemToCart(userId, productId, 2, true);

        when(cartService.getCartItems(userId)).thenReturn(cartItems);

        List<CartItem> result = cartUseCase.addItemAndReturnCartItems(userId, productId, 2, true);

        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProductId());
        verify(cartService, times(1)).addItemToCart(userId, productId, 2, true);
        verify(cartService, times(1)).getCartItems(userId);
    }

    @Test
    public void 장바구니에서_아이템_삭제_예외_테스트() {
        doThrow(new NoSuchElementException("해당 아이템이 장바구니에 없습니다."))
                .when(cartService).removeItemFromCart(userId, cartItemId);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            cartUseCase.removeItemAndReturnCartItems(userId, cartItemId);
        });

        assertEquals("해당 아이템이 장바구니에 없습니다.", exception.getMessage());

        verify(cartService, times(1)).removeItemFromCart(userId, cartItemId);
    }

    @Test
    public void 장바구니에서_아이템_삭제() {
        doNothing().when(cartService).removeItemFromCart(userId, cartItemId);
        List<CartItem> cartItemsAfterDeletion = Collections.emptyList();
        when(cartService.getCartItems(userId)).thenReturn(cartItemsAfterDeletion);

        List<CartItem> result = cartUseCase.removeItemAndReturnCartItems(userId, cartItemId);

        assertEquals(0, result.size());
        verify(cartService, times(1)).removeItemFromCart(userId, cartItemId);
        verify(cartService, times(1)).getCartItems(userId);
    }

    @Test
    public void 장바구니_아이템_조회() {
        CartItem cartItem = new CartItem(1L, productId, 2, true, LocalDateTime.now());
        List<CartItem> cartItems = Collections.singletonList(cartItem);

        when(cartService.getCartItems(userId)).thenReturn(cartItems);

        List<CartItem> result = cartUseCase.getCartItems(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productId, result.get(0).getProductId());
        verify(cartService, times(1)).getCartItems(userId);
    }
}