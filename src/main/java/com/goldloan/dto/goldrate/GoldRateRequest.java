package com.goldloan.dto.goldrate;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class GoldRateRequest {
    @NotNull @Min(1) private Integer karat;
    @NotNull @Min(1) private Long ratePerGramPaise;
    private String effectiveDate;
}
