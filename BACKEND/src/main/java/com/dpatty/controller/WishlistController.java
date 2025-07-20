package com.dpatty.controller;

import com.dpatty.dto.wishlist.WishlistResponse;
import com.dpatty.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
@Tag(name = "Wishlist", description = "Gestión de lista de favoritos")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Obtener lista de favoritos del usuario")
    public ResponseEntity<Page<WishlistResponse>> getUserWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WishlistResponse> wishlist = wishlistService.getUserWishlist(pageable);
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/add/{productId}")
    @Operation(summary = "Agregar producto a favoritos")
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long productId) {
        WishlistResponse wishlistItem = wishlistService.addToWishlist(productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistItem);
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Eliminar producto de favoritos")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok().body("Producto eliminado de favoritos");
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Verificar si producto está en favoritos")
    public ResponseEntity<?> isInWishlist(@PathVariable Long productId) {
        boolean inWishlist = wishlistService.isInWishlist(productId);
        return ResponseEntity.ok().body(new Object() {
            public final boolean inWishlist = inWishlist;
        });
    }
}