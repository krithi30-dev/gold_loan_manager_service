package com.goldloan.dto.loan;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoanResponse {
    private UUID id;
    private String loanNumber;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private Long loanAmountPaise;
    private Long outstandingAmountPaise;
    private Long interestAccruedPaise;
    private Long totalDuePaise;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private String status;
    private String branchId;
    private String branchName;
    private LocalDate dueDate;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private List<JewelleryItemResponse> jewelleryItems;
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class JewelleryItemResponse {
        private UUID id;
        private String itemType;
        private Double weightGrams;
        private Integer purityKarat;
        private Long estimatedValuePaise;
        private String imageUrl;
        private String status;
    }
}
