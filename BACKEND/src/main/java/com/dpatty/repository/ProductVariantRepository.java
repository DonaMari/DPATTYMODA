package com.dpatty.repository;

import com.dpatty.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductIdAndIsActiveTrue(Long productId);
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.stockQuantity <= pv.minStockLevel")
    List<ProductVariant> findLowStockVariants();
    
    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.size = :size AND pv.color = :color")
    ProductVariant findByProductIdAndSizeAndColor(@Param("productId") Long productId, 
                                                 @Param("size") String size, 
                                                 @Param("color") String color);
    
    Boolean existsBySku(String sku);
}