package com.goldloan.dto.repayment;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class RepaymentRequest {
    @NotNull private String loanId;
    @NotNull @Min(1) private Long amountPaise;
    @NotNull private String paymentMode;
    private String notes;
}
