package com.fincommerce.service;

import com.fincommerce.dto.CartDtos;
import com.fincommerce.entity.*;
import com.fincommerce.exception.BadRequestException;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }

    public CartDtos.CartDto getCartDto(Long userId, String couponCode) {
        Cart cart = getOrCreateCart(userId);
        CartDtos.CartDto dto = new CartDtos.CartDto();
        dto.setCartId(cart.getId());

        List<CartDtos.CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            CartDtos.CartItemDto itemDto = new CartDtos.CartItemDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setProductBrand(item.getProduct().getBrand());
            itemDto.setProductImage(item.getProduct().getImageUrl());

            BigDecimal unitPrice = item.getProduct().getPrice();
            if (item.getProduct().getDiscountPercent() != null && item.getProduct().getDiscountPercent() > 0) {
                BigDecimal discount = BigDecimal.valueOf(100 - item.getProduct().getDiscountPercent())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                unitPrice = unitPrice.multiply(discount);
            }
            itemDto.setUnitPrice(unitPrice.setScale(2, RoundingMode.HALF_UP));
            itemDto.setQuantity(item.getQuantity());

            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);
            itemDto.setSubtotal(itemSubtotal);

            subtotal = subtotal.add(itemSubtotal);
            itemDtos.add(itemDto);
        }

        dto.setItems(itemDtos);
        dto.setSubtotal(subtotal);

        // Apply coupon discount if provided
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCaseAndIsActiveTrue(couponCode.trim());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (subtotal.compareTo(coupon.getMinOrderValue()) >= 0) {
                    if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
                        discountAmount = subtotal.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
                        if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                            discountAmount = coupon.getMaxDiscountAmount();
                        }
                    } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType())) {
                        discountAmount = coupon.getDiscountValue();
                    }
                    dto.setAppliedCouponCode(coupon.getCode());
                }
            }
        }
        dto.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));

        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(new BigDecimal("1000")) < 0
                ? new BigDecimal("50.00") : BigDecimal.ZERO;
        dto.setDeliveryFee(deliveryFee);

        BigDecimal taxAmount = subtotal.subtract(discountAmount).multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        if (taxAmount.compareTo(BigDecimal.ZERO) < 0) taxAmount = BigDecimal.ZERO;
        dto.setTaxAmount(taxAmount);

        BigDecimal finalTotal = subtotal.subtract(discountAmount).add(deliveryFee).add(taxAmount).setScale(2, RoundingMode.HALF_UP);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;
        dto.setFinalTotal(finalTotal);

        return dto;
    }

    @Transactional
    public void addToCart(Long userId, CartDtos.AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, product, request.getQuantity());
            cart.getItems().add(newItem);
            cartRepository.save(cart);
        }
    }

    @Transactional
    public void updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Cart item not found for user");
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Cart item not found for user");
        }
        cartItemRepository.delete(item);
    }

    // Wishlist functions
    public Wishlist getOrCreateWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Wishlist wishlist = new Wishlist(user);
                    return wishlistRepository.save(wishlist);
                });
    }

    @Transactional
    public void toggleWishlist(Long userId, Long productId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<WishlistItem> itemOpt = wishlistItemRepository.findByWishlistAndProduct(wishlist, product);
        if (itemOpt.isPresent()) {
            wishlistItemRepository.delete(itemOpt.get());
        } else {
            WishlistItem newItem = new WishlistItem(wishlist, product);
            wishlist.getItems().add(newItem);
            wishlistRepository.save(wishlist);
        }
    }
}
