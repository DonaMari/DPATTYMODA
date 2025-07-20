package com.dpatty.service;

import com.dpatty.dto.wishlist.WishlistResponse;
import com.dpatty.model.Product;
import com.dpatty.model.User;
import com.dpatty.model.Wishlist;
import com.dpatty.repository.ProductRepository;
import com.dpatty.repository.WishlistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    public Page<WishlistResponse> getUserWishlist(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        Page<Wishlist> wishlist = wishlistRepository.findByUserIdWithProduct(currentUser.getId(), pageable);
        return wishlist.map(item -> modelMapper.map(item, WishlistResponse.class));
    }

    @Transactional
    public WishlistResponse addToWishlist(Long productId) {
        User currentUser = authService.getCurrentUser();
        
        if (wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId)) {
            throw new RuntimeException("El producto ya está en tu lista de favoritos");
        }
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Wishlist wishlistItem = new Wishlist();
        wishlistItem.setUser(currentUser);
        wishlistItem.setProduct(product);
        
        Wishlist savedItem = wishlistRepository.save(wishlistItem);
        return modelMapper.map(savedItem, WishlistResponse.class);
    }

    @Transactional
    public void removeFromWishlist(Long productId) {
        User currentUser = authService.getCurrentUser();
        
        if (!wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId)) {
            throw new RuntimeException("El producto no está en tu lista de favoritos");
        }
        
        wishlistRepository.deleteByUserIdAndProductId(currentUser.getId(), productId);
    }

    public boolean isInWishlist(Long productId) {
        User currentUser = authService.getCurrentUser();
        return wishlistRepository.existsByUserIdAndProductId(currentUser.getId(), productId);
    }
}