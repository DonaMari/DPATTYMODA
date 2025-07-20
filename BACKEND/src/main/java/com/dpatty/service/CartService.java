package com.dpatty.service;

import com.dpatty.dto.cart.CartResponse;
import com.dpatty.dto.cart.AddToCartRequest;
import com.dpatty.model.*;
import com.dpatty.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    public CartResponse getCart() {
        User currentUser = authService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser.getId(), null);
        return convertToResponse(cart);
    }

    public CartResponse getGuestCart(String sessionId) {
        Cart cart = getOrCreateCart(null, sessionId);
        return convertToResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User currentUser = authService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser.getId(), null);
        
        ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Variante de producto no encontrada"));

        // Check stock availability
        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + variant.getStockQuantity());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            if (variant.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + variant.getStockQuantity());
            }
            
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newItem);
        }

        return convertToResponse(cart);
    }

    @Transactional
    public CartResponse addToGuestCart(String sessionId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(null, sessionId);
        
        ProductVariant variant = variantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Variante de producto no encontrada"));

        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente");
        }

        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newItem);
        }

        return convertToResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long itemId, Integer quantity) {
        User currentUser = authService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser.getId(), null);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("No autorizado para modificar este item");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            if (item.getProductVariant().getStockQuantity() < quantity) {
                throw new RuntimeException("Stock insuficiente");
            }
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return convertToResponse(cart);
    }

    @Transactional
    public void removeFromCart(Long itemId) {
        User currentUser = authService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser.getId(), null);
        
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("No autorizado para eliminar este item");
        }

        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart() {
        User currentUser = authService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser.getId(), null);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private Cart getOrCreateCart(Long userId, String sessionId) {
        Optional<Cart> existingCart;
        
        if (userId != null) {
            existingCart = cartRepository.findByUserIdWithItems(userId);
        } else {
            existingCart = cartRepository.findBySessionId(sessionId);
        }

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Create new cart
        Cart newCart = new Cart();
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            newCart.setUser(user);
        } else {
            newCart.setSessionId(sessionId);
        }
        
        return cartRepository.save(newCart);
    }

    private CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setItems(cart.getItems().stream()
                .map(item -> {
                    CartResponse.CartItemResponse itemResponse = new CartResponse.CartItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setProductVariant(modelMapper.map(item.getProductVariant(), com.dpatty.dto.product.ProductVariantResponse.class));
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setSubtotal(item.getProductVariant().getPrice() != null ? 
                        item.getProductVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())) :
                        item.getProductVariant().getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return itemResponse;
                }).toList());

        // Calculate totals
        BigDecimal total = response.getItems().stream()
                .map(CartResponse.CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        response.setTotal(total);
        response.setItemCount(response.getItems().size());
        
        return response;
    }
}