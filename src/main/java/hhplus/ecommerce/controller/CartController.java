package hhplus.ecommerce.controller;

import hhplus.ecommerce.application.cart.CartService;
import hhplus.ecommerce.domain.cart.CartItem;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/items")
    public ResponseEntity<?> addItemToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam(defaultValue = "false") boolean isSelected) {
        try {
            cartService.addItemToCart(userId, productId, quantity, isSelected);
            return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품이 존재하지 않습니다.");
        }
    }

    @PutMapping("/{userId}/items/{productId}/update")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        try {
            cartService.updateItemQuantity(userId, productId, quantity);
            return ResponseEntity.ok("상품 수량이 업데이트되었습니다.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("장바구니에 해당 상품이 없습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<?> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        try {
            cartService.removeItemFromCart(userId, cartItemId);
            return ResponseEntity.ok("상품이 장바구니에서 삭제되었습니다.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 아이템이 장바구니에 없습니다.");
        }
    }

    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }
}
