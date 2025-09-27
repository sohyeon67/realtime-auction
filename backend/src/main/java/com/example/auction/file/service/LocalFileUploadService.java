package com.example.auction.file.service;

import com.example.auction.file.dto.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileUploadService implements FileUploadService {

    @Value("${file.dir}")
    private String fileDir;

    @Override
    public UploadFile upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String savedFilename = createSavedFilename(originalFilename);
        String filePath = getFullPath(savedFilename);
        file.transferTo(new File(getFullPath(savedFilename))); // 업로드
        return new UploadFile(originalFilename, savedFilename, filePath);
    }



    @Override
    public List<UploadFile> uploadAll(List<MultipartFile> files) throws IOException {
        List<UploadFile> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                savedFiles.add(upload(file));
            }
        }

        return savedFiles;
    }

    @Override
    public void delete(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    private String getFullPath(String filename) {
        return Paths.get(fileDir, filename).toString();
    }

    private String createSavedFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) {
            return "";
        }
        return originalFilename.substring(pos + 1);
    }
}
