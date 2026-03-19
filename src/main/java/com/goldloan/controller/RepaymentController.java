package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.dto.repayment.*;
import com.goldloan.service.RepaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/repayments")
@RequiredArgsConstructor
public class RepaymentController {
    private final RepaymentService repaymentService;
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN','GOLD_LOAN_EXECUTIVE')")
    public ResponseEntity<ApiResponse<RepaymentResponse>> record(@Valid @RequestBody RepaymentRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(repaymentService.record(req, auth.getName()), "Repayment recorded"));
    }
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<ApiResponse<Page<RepaymentResponse>>> getByLoan(
            @PathVariable UUID loanId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(repaymentService.getByLoan(loanId, PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable UUID id) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt.txt")
                .contentType(MediaType.TEXT_PLAIN).body(repaymentService.getReceiptPdf(id));
    }
}
