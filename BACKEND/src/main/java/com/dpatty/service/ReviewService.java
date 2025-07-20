package com.dpatty.service;

import com.dpatty.dto.review.CreateReviewRequest;
import com.dpatty.dto.review.ReviewResponse;
import com.dpatty.model.Product;
import com.dpatty.model.Review;
import com.dpatty.model.User;
import com.dpatty.repository.ProductRepository;
import com.dpatty.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductIdAndStatusOrderByCreatedAtDesc(
            productId, Review.ReviewStatus.APPROVED, pageable);
        return reviews.map(review -> modelMapper.map(review, ReviewResponse.class));
    }

    public Page<ReviewResponse> getPendingReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByStatusOrderByCreatedAtDesc(
            Review.ReviewStatus.PENDING, pageable);
        return reviews.map(review -> modelMapper.map(review, ReviewResponse.class));
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        User currentUser = authService.getCurrentUser();
        
        // Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(currentUser.getId(), request.getProductId())) {
            throw new RuntimeException("Ya has calificado este producto");
        }
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(currentUser);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus(Review.ReviewStatus.PENDING);
        review.setIsVerifiedPurchase(false); // TODO: Check if user actually purchased the product
        
        Review savedReview = reviewRepository.save(review);
        
        // Notify admins about new review
        notificationService.notifyAdmins(
            "Nueva Reseña Pendiente",
            "Nueva reseña para " + product.getName() + " requiere moderación",
            "INFO"
        );
        
        return modelMapper.map(savedReview, ReviewResponse.class);
    }

    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        
        review.setStatus(Review.ReviewStatus.APPROVED);
        Review savedReview = reviewRepository.save(review);
        
        // Notify user
        notificationService.createNotification(
            review.getUser().getId(),
            "Reseña Aprobada",
            "Tu reseña para " + review.getProduct().getName() + " ha sido aprobada",
            "SUCCESS"
        );
        
        return modelMapper.map(savedReview, ReviewResponse.class);
    }

    @Transactional
    public ReviewResponse rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        
        review.setStatus(Review.ReviewStatus.REJECTED);
        Review savedReview = reviewRepository.save(review);
        
        // Notify user
        notificationService.createNotification(
            review.getUser().getId(),
            "Reseña Rechazada",
            "Tu reseña para " + review.getProduct().getName() + " no cumple con nuestras políticas",
            "WARNING"
        );
        
        return modelMapper.map(savedReview, ReviewResponse.class);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        
        User currentUser = authService.getCurrentUser();
        if (!review.getUser().getId().equals(currentUser.getId()) && !hasAdminRole(currentUser)) {
            throw new RuntimeException("No autorizado para eliminar esta reseña");
        }
        
        reviewRepository.delete(review);
    }

    public Double getProductAverageRating(Long productId) {
        return reviewRepository.getAverageRatingByProductId(productId);
    }

    public Long getProductReviewCount(Long productId) {
        return reviewRepository.getReviewCountByProductId(productId);
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "EMPLOYEE".equals(role.getName()));
    }
}