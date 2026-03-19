package com.goldloan.dto.user;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class UserRequest {
    @NotBlank private String name;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String role;
    private String branchId;
}
