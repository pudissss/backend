package com.farmterra.backend.repository;

import com.farmterra.backend.model.Product;
import com.farmterra.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFarmer(User farmer);
    List<Product> findByCategory(String category);
    List<Product> findByFeaturedTrue();
    List<Product> findByOrganicTrue();

    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findAvailableProducts();
}
