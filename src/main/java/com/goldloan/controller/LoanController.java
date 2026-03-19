package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.dto.loan.*;
import com.goldloan.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN','GOLD_LOAN_EXECUTIVE')")
    public ResponseEntity<ApiResponse<LoanResponse>> create(@Valid @RequestBody LoanRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(loanService.createLoan(req, auth.getName()), "Loan created"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getById(id)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getAll(status, PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<LoanResponse>>> getByCustomer(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getByCustomer(customerId, PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN','GOLD_LOAN_EXECUTIVE')")
    public ResponseEntity<ApiResponse<LoanResponse>> close(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(loanService.closeLoan(id, auth.getName()), "Loan closed"));
    }
    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable UUID id) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loan-receipt.txt")
                .contentType(MediaType.TEXT_PLAIN).body(loanService.getLoanReceiptPdf(id));
    }
}
