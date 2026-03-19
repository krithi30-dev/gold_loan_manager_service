package com.goldloan.dto.repayment;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RepaymentResponse {
    private UUID id;
    private String loanId;
    private String loanNumber;
    private Long amountPaise;
    private LocalDateTime paymentDate;
    private String paymentMode;
    private String receiptNumber;
    private String notes;
    private LocalDateTime createdAt;
}
