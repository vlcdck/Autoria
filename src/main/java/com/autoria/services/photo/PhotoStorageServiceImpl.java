package com.autoria.services.photo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class PhotoStorageServiceImpl implements PhotoStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String savePhoto(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dirPath = getUploadPath();
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        Path filePath = dirPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + fileName;  // URL для доступу через веб
    }

    @Override
    public void deletePhoto(String photoUrl) throws IOException {
        String fileName = Paths.get(photoUrl).getFileName().toString();
        Path filePath = getUploadPath().resolve(fileName);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    private Path getUploadPath() {
        // Замінимо ${user.home} на реальний шлях
        String resolvedPath = uploadDir.replace("${user.home}", System.getProperty("user.home"));
        return Paths.get(resolvedPath);
    }
}
