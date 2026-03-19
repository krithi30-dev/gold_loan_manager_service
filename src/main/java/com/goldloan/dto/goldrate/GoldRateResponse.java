package com.goldloan.dto.goldrate;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GoldRateResponse {
    private UUID id;
    private Integer karat;
    private Long ratePerGramPaise;
    private LocalDate effectiveDate;
    private LocalDateTime createdAt;
}
