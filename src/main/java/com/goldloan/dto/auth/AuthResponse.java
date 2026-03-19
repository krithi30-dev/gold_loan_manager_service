package com.goldloan.dto.auth;
import lombok.*;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default private String tokenType = "Bearer";
    private UUID userId;
    private String name;
    private String email;
    private String role;
    private UUID branchId;
    private String branchName;
}
