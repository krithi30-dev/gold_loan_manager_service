package com.goldloan.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
@Slf4j
@Component
public class FileStorageUtil {
    @Value("${file.upload-dir:uploads}") private String uploadDir;
    public String store(MultipartFile file, String folder) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dir = Paths.get(uploadDir, folder);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return folder + "/" + filename;
    }
    public byte[] load(String path) throws IOException {
        return Files.readAllBytes(Paths.get(uploadDir, path));
    }
}
