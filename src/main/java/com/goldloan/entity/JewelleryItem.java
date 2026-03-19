package com.goldloan.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jewellery_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JewelleryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "item_type", nullable = false)
    private String itemType;

    @Column(name = "weight_grams", nullable = false)
    private Double weightGrams;

    @Column(name = "purity_karat", nullable = false)
    private Integer purityKarat;

    @Column(name = "estimated_value_paise", nullable = false)
    private Long estimatedValuePaise;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PLEDGED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
