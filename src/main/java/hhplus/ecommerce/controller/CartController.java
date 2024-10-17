package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.cart.CartItemDto;
import hhplus.ecommerce.application.cart.CartService;
import hhplus.ecommerce.domain.cart.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/items")
    public ResponseEntity<String> addItemToCart(@PathVariable Long userId, @RequestBody CartItemDto cartItemDto) {
        cartService.addItemToCart(userId, cartItemDto.getProductId(), cartItemDto.getQuantity(), cartItemDto.isSelected());
        return ResponseEntity.ok("장바구니에 추가되었습니다.");
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long userId, @PathVariable Long cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }
}
