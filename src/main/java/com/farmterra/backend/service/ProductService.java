package com.farmterra.backend.service;

import com.farmterra.backend.model.Product;
import com.farmterra.backend.model.User;
import com.farmterra.backend.repository.ProductRepository;
import com.farmterra.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Product createProduct(Product product) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User farmer = userRepository.findByEmail(currentUsername)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        // Set farmer for the product
        product.setFarmer(farmer);
        
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, Product productDetails) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Update product details
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setUnit(productDetails.getUnit());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setImages(productDetails.getImages());
        product.setOrganic(productDetails.getOrganic());
        
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        productRepository.delete(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    public List<Product> getOrganicProducts() {
        return productRepository.findByOrganicTrue();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> getProductsByFarmer(User farmer) {
        return productRepository.findByFarmer(farmer);
    }
}
