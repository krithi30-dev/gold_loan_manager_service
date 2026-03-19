package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.dto.user.*;
import com.goldloan.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_ADMIN')")
public class UserController {
    private final UserService userService;
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userService.create(req), "User created"));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getById(id)));
    }
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.toggleActive(id), "Status toggled"));
    }
}
