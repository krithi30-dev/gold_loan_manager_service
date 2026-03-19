package com.goldloan.dto.user;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String role;
    private String branchId;
    private String branchName;
    private boolean active;
    private LocalDateTime createdAt;
}
