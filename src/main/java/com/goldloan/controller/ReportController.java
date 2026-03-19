package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.dto.report.DashboardResponse;
import com.goldloan.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN')")
public class ReportController {
    private final ReportService reportService;
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(@RequestParam UUID branchId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDashboard(branchId)));
    }
}
