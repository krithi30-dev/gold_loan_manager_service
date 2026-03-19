package com.goldloan.dto.loan;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
@Data
public class LoanRequest {
    @NotNull private String customerId;
    @NotNull @DecimalMin("0.1") private BigDecimal interestRate;
    @NotNull @Min(1) private Integer tenureMonths;
    private Integer ltvPercent = 75;
    private String branchId;
    @NotEmpty private List<JewelleryItemRequest> jewelleryItems;
    @Data
    public static class JewelleryItemRequest {
        @NotBlank private String itemType;
        @NotNull @DecimalMin("0.001") private Double weightGrams;
        @NotNull private Integer purityKarat;
        private String imageUrl;
    }
}
