package com.dpatty.repository;

import com.dpatty.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNullAndIsActiveTrueOrderBySortOrder();
    
    List<Category> findByParentIdAndIsActiveTrueOrderBySortOrder(Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.sortOrder")
    List<Category> findAllActiveOrderBySortOrder();
}