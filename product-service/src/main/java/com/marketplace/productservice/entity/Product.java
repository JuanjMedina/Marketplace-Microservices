package com.marketplace.productservice.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    @Column(nullable = true)
    private String imageUrl;

     private String category;

    @Column(nullable = false)
    private String sellerId;

    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
