package hhplus.ecommerce.application.cart;

import hhplus.ecommerce.domain.cart.Cart;
import hhplus.ecommerce.domain.cart.CartItem;
import hhplus.ecommerce.infrastructure.repository.CartItemRepository;
import hhplus.ecommerce.infrastructure.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void 장바구니에_상품_추가_성공() {
        Long userId = 1L;
        Long productId = 1L;
        int quantity = 2;

        Cart cart = new Cart(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.addItemToCart(userId, productId, quantity, true);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void 장바구니_아이템_조회_성공() {
        Long userId = 1L;
        Long productId = 1L;
        Cart cart = new Cart(userId);
        CartItem cartItem = new CartItem(cart.getId(), productId, 2, true, LocalDateTime.now());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cart.getId())).thenReturn(List.of(cartItem));

        List<CartItem> items = cartService.getCartItems(userId);

        assertEquals(1, items.size());
        assertEquals(productId, items.get(0).getProductId());
    }

    @Test
    void 장바구니_아이템_삭제_성공() {
        Long userId = 1L;
        Long cartItemId = 1L;

        Cart cart = new Cart(userId);
        cartRepository.save(cart);

        CartItem cartItem = new CartItem(cart.getId(),
                1L,
                2,
                true,
                LocalDateTime.now());
        cartItemRepository.save(cartItem);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart(userId, cartItemId);

        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void 장바구니에_상품_추가_이미_있는_상품은_수량_증가() {
        Long userId = 1L;
        Long productId = 1L;
        int quantityToAdd = 2;
        String options = null;

        Cart cart = new Cart(userId);
        CartItem existingItem = new CartItem(cart.getId(), productId, 1, true, LocalDateTime.now());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.of(existingItem));

        cartService.addItemToCart(userId, productId, quantityToAdd, existingItem.isSelected());

        assertEquals(3, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }
}