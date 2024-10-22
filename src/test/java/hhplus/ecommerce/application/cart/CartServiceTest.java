package hhplus.ecommerce.application.cart;

import hhplus.ecommerce.domain.cart.Cart;
import hhplus.ecommerce.domain.cart.CartItem;
import hhplus.ecommerce.domain.product.Product;
import hhplus.ecommerce.infrastructure.repository.CartItemRepository;
import hhplus.ecommerce.infrastructure.repository.CartRepository;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

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
    void 존재하지_않는_상품을_장바구니에_넣을_경우_예외_발생() {
        Long userId = 1L;
        Long productId = 999L;
        int quantity = 1;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            cartService.addItemToCart(userId, productId, quantity, true);
        });

        assertEquals("상품이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void 장바구니에_상품_추가_성공() {
        Long userId = 1L;
        Long productId = 1L;
        int quantity = 2;

        Product product = new Product(productId, "청바지", "알록달록 청바지", BigDecimal.valueOf(1000), 10, LocalDateTime.now(), 0);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product)); // 상품이 존재한다고 설정

        Cart cart = new Cart(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.addItemToCart(userId, productId, quantity, true);

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
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

        Cart cart = new Cart(userId);

        CartItem existingItem = new CartItem(cart.getId(), productId, 1, true, LocalDateTime.now());

        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.of(existingItem));

        cartService.addItemToCart(userId, productId, quantityToAdd, existingItem.isSelected());

        assertEquals(3, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }


    @Test
    void 장바구니_상품_수량_수정_성공() {
        Long userId = 1L;
        Long productId = 1L;
        int newQuantity = 2;

        Cart cart = new Cart(userId);
        CartItem existingItem = new CartItem(cart.getId(), productId, 3, true, LocalDateTime.now());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.of(existingItem));

        cartService.updateItemQuantity(userId, productId, newQuantity);

        assertEquals(newQuantity, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void 장바구니_상품_수량_수정_시_수량이_1보다_작으면_예외_발생() {
        Long userId = 1L;
        Long productId = 1L;
        int newQuantity = 0;

        Cart cart = new Cart(userId);
        CartItem existingItem = new CartItem(cart.getId(), productId, 3, true, LocalDateTime.now());

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)).thenReturn(Optional.of(existingItem));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cartService.updateItemQuantity(userId, productId, newQuantity);
        });

        assertEquals("수량은 1 이하로 줄일 수 없습니다.", exception.getMessage());
    }
}
