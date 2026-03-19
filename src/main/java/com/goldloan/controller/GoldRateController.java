package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.dto.goldrate.*;
import com.goldloan.service.GoldRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/gold-rates")
@RequiredArgsConstructor
public class GoldRateController {
    private final GoldRateService goldRateService;
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN')")
    public ResponseEntity<ApiResponse<GoldRateResponse>> setRate(@Valid @RequestBody GoldRateRequest req, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(goldRateService.setRate(req, auth.getName()), "Rate set"));
    }
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<GoldRateResponse>>> getToday() {
        return ResponseEntity.ok(ApiResponse.success(goldRateService.getTodayRates()));
    }
}
