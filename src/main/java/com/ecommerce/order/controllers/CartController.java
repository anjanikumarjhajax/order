package com.ecommerce.order.controllers;

import com.ecommerce.order.dtos.CartItemRequest;
import com.ecommerce.order.models.Cartitem;
import com.ecommerce.order.services.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String UserId,
            @RequestBody CartItemRequest request) {
        if (!cartService.addToCart(UserId, request)) {
            ;
            return ResponseEntity.badRequest().body("Product out of stock or User not found or Product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> deleteFromCart(@RequestHeader("X-User-Id") String userId,
                                               @PathVariable String productId) {
        boolean deleted = cartService.deleteItemFromCart(userId, productId);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();


    }

    @GetMapping
    public ResponseEntity<List<Cartitem>> getCart(
            @RequestHeader("X-User-Id") String userId) {
        log.info("Cart details for userID: {}", userId);
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
