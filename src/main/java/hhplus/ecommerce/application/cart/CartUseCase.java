package hhplus.ecommerce.application.cart;

import hhplus.ecommerce.domain.cart.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartUseCase {

    private final CartService cartService;

    // 장바구니에 아이템 추가 후 아이템 목록 반환
    public List<CartItem> addItemAndReturnCartItems(Long userId, Long productId, int quantity, boolean isSelected) {
        cartService.addItemToCart(userId, productId, quantity, isSelected);
        return cartService.getCartItems(userId);
    }

    // 장바구니에서 아이템 삭제 후 아이템 목록 반환
    public List<CartItem> removeItemAndReturnCartItems(Long userId, Long cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);
        return cartService.getCartItems(userId);
    }

    // 장바구니 아이템 목록 조회
    public List<CartItem> getCartItems(Long userId) {
        return cartService.getCartItems(userId);
    }
}
