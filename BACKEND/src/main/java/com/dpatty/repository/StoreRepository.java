package com.dpatty.repository;

import com.dpatty.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByIsActiveTrueOrderByName();
    Optional<Store> findByIsMainTrue();
}