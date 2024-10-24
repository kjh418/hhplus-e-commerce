package hhplus.ecommerce.application.cart;

import hhplus.ecommerce.domain.cart.Cart;
import hhplus.ecommerce.domain.cart.CartItem;
import hhplus.ecommerce.infrastructure.repository.CartItemRepository;
import hhplus.ecommerce.infrastructure.repository.CartRepository;
import hhplus.ecommerce.infrastructure.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId);
                    cartRepository.save(newCart);
                    return newCart;
                });
    }

    @Transactional
    public void addItemToCart(Long userId, Long productId, int quantity, boolean isSelected) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        Cart cart = getCartByUserId(userId);
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.updateQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem(cart.getId(), productId, quantity, isSelected, LocalDateTime.now());
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void updateItemQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NoSuchElementException("장바구니에 해당 상품이 없습니다."));

        if (newQuantity < 1) {
            throw new IllegalStateException("수량은 1 이하로 줄일 수 없습니다.");
        } else {
            cartItem.updateQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeItemFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NoSuchElementException("해당 아이템이 장바구니에 없습니다."));

        cartItemRepository.delete(cartItem);
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCartByUserId(userId);
        return cartItemRepository.findByCartId(cart.getId());
    }
}
