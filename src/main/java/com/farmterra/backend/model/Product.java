package com.farmterra.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @Column(length = 1000)
    private String description;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String unit;

    @Positive(message = "Stock must be non-negative")
    private Integer stock;

    private String category;

    @ElementCollection
    private List<String> images;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private User farmer;

    private Boolean organic;
    private Boolean featured;

    private Double rating;
    private Integer reviewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        rating = rating == null ? 0.0 : rating;
        reviewCount = reviewCount == null ? 0 : reviewCount;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
