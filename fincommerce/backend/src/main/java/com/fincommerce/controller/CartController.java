package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.dto.CartDtos;
import com.fincommerce.security.UserPrincipal;
import com.fincommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<CartDtos.CartDto>> getCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) String couponCode) {
        CartDtos.CartDto cart = cartService.getCartDto(userPrincipal.getId(), couponCode);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cart));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<ApiResponse<String>> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CartDtos.AddToCartRequest request) {
        cartService.addToCart(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart"));
    }

    @PutMapping("/cart/items/{id}")
    public ResponseEntity<ApiResponse<String>> updateCartItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody CartDtos.UpdateCartItemRequest request) {
        cartService.updateCartItem(userPrincipal.getId(), id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart item updated"));
    }

    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<ApiResponse<String>> removeCartItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        cartService.removeCartItem(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Cart item removed"));
    }

    @PostMapping("/wishlist/toggle/{productId}")
    public ResponseEntity<ApiResponse<String>> toggleWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        cartService.toggleWishlist(userPrincipal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Wishlist updated"));
    }
}
