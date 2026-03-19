package com.goldloan.dto.customer;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String customerCode;
    private String name;
    private LocalDate dob;
    private String phone;
    private String email;
    private String aadhaar;
    private String pan;
    private String address;
    private String photoUrl;
    private String signatureUrl;
    private String branchId;
    private String branchName;
    private LocalDateTime createdAt;
    private List<KycDocResponse> kycDocuments;
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class KycDocResponse {
        private UUID id;
        private String documentType;
        private String fileUrl;
    }
}
