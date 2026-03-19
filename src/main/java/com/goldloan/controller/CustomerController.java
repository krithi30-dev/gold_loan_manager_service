package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.dto.customer.*;
import com.goldloan.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN','GOLD_LOAN_EXECUTIVE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(customerService.create(req, auth.getName()), "Customer created"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getById(id)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(q != null ? customerService.search(q, pageable) : customerService.getAll(pageable)));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN','GOLD_LOAN_EXECUTIVE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(@PathVariable UUID id, @RequestBody CustomerRequest req, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(customerService.update(id, req, auth.getName()), "Updated"));
    }
}
