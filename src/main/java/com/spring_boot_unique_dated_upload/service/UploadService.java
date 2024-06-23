package com.spring_boot_unique_dated_upload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;

@Service
public class UploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path getUploadPath() {
        LocalDate today = LocalDate.now();
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());
        return Paths.get(uploadDir, String.valueOf(today.getYear()), month, day);
    }

    private Path getUniqueFilePath(Path directory, String fileName) {
        Path filePath = directory.resolve(fileName);
        String fileBaseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String fileExtension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";
        int counter = 1;
        while (Files.exists(filePath)) {
            String newFileName = fileBaseName + "_" + counter + fileExtension;
            filePath = directory.resolve(newFileName);
            counter++;
        }
        return filePath;
    }

    public String storeFile(String fileName, String base64) throws IOException {
        byte[] data = Base64.getDecoder().decode(base64);
        Path uploadPath = getUploadPath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = getUniqueFilePath(uploadPath, fileName);
        Files.write(filePath, data);

        return filePath.toString();
    }
}