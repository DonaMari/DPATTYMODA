package com.dpatty.controller;

import com.dpatty.dto.cart.AddToCartRequest;
import com.dpatty.dto.cart.CartResponse;
import com.dpatty.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Gestión del carrito de compras")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @Operation(summary = "Obtener carrito del usuario")
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/guest/{sessionId}")
    @Operation(summary = "Obtener carrito de invitado")
    public ResponseEntity<CartResponse> getGuestCart(@PathVariable String sessionId) {
        CartResponse cart = cartService.getGuestCart(sessionId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    @Operation(summary = "Agregar producto al carrito")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(request);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/guest/{sessionId}/add")
    @Operation(summary = "Agregar producto al carrito de invitado")
    public ResponseEntity<CartResponse> addToGuestCart(@PathVariable String sessionId,
                                                      @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToGuestCart(sessionId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de item en carrito")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long itemId,
                                                      @RequestParam Integer quantity) {
        CartResponse cart = cartService.updateCartItem(itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar item del carrito")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return ResponseEntity.ok().body("Item eliminado del carrito");
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Vaciar carrito")
    public ResponseEntity<?> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok().body("Carrito vaciado");
    }
}