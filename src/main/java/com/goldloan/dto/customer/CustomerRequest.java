package com.goldloan.dto.customer;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class CustomerRequest {
    @NotBlank private String name;
    private String dob;
    @NotBlank @Pattern(regexp = "^[0-9]{10}$", message = "Must be 10 digits") private String phone;
    @Email private String email;
    @Pattern(regexp = "^[0-9]{12}$", message = "Must be 12 digits") private String aadhaar;
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN") private String pan;
    private String address;
    private String photoUrl;
    private String signatureUrl;
    private String branchId;
}
