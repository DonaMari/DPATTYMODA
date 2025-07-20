package com.dpatty.controller;

import com.dpatty.dto.review.CreateReviewRequest;
import com.dpatty.dto.review.ReviewResponse;
import com.dpatty.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Gestión de reseñas y calificaciones")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Obtener reseñas de un producto")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Obtener reseñas pendientes de moderación")
    public ResponseEntity<Page<ReviewResponse>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getPendingReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    @Operation(summary = "Crear nueva reseña")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Aprobar reseña")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Long id) {
        ReviewResponse review = reviewService.approveReview(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Rechazar reseña")
    public ResponseEntity<ReviewResponse> rejectReview(@PathVariable Long id) {
        ReviewResponse review = reviewService.rejectReview(id);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().body("Reseña eliminada exitosamente");
    }

    @GetMapping("/product/{productId}/stats")
    @Operation(summary = "Obtener estadísticas de reseñas de un producto")
    public ResponseEntity<?> getProductReviewStats(@PathVariable Long productId) {
        Double averageRating = reviewService.getProductAverageRating(productId);
        Long reviewCount = reviewService.getProductReviewCount(productId);
        
        return ResponseEntity.ok().body(new Object() {
            public final Double averageRating = averageRating != null ? averageRating : 0.0;
            public final Long reviewCount = reviewCount != null ? reviewCount : 0L;
        });
    }
}