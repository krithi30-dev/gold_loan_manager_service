package com.goldloan.controller;
import com.goldloan.dto.ApiResponse;
import com.goldloan.util.FileStorageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageUtil fileStorageUtil;
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) throws IOException {
        String path = fileStorageUtil.store(file, folder);
        return ResponseEntity.ok(ApiResponse.success(Map.of("path", path, "url", "/api/files/" + path)));
    }
    @GetMapping("/**")
    public ResponseEntity<byte[]> getFile(HttpServletRequest request) throws IOException {
        String path = request.getRequestURI().replace("/api/files/", "");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(fileStorageUtil.load(path));
    }
}
