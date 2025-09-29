package com.example.auction.file.service;

import com.example.auction.file.domain.FileCategory;
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

/**
 * 서버 디스크에 바로 저장하는 방식
 */
@Service
public class LocalFileUploadService implements FileUploadService {

    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @Override
    public UploadFile upload(MultipartFile file, FileCategory category) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String savedFilename = createSavedFilename(originalFilename);
        String relativePath = getRelativePath(savedFilename, category);
        String fullPath = getFullPath(savedFilename, category);

        createDirectoryIfNotExists(category);

        file.transferTo(new File(fullPath)); // 업로드

        return new UploadFile(originalFilename, savedFilename, relativePath);
    }

    @Override
    public List<UploadFile> uploadAll(List<MultipartFile> files, FileCategory category) throws IOException {
        List<UploadFile> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                savedFiles.add(upload(file, category));
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

    @Override
    public String getFileUrl(String relativePath) {
        return baseUrl + "/" + relativePath.replace("\\", "/");
    }



    // 폴더 없으면 생성
    private void createDirectoryIfNotExists(FileCategory category) throws IOException {
        Path dirPath = Paths.get(fileDir, category.getFolder());
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    // 파일 전체 경로 반환
    private String getFullPath(String filename, FileCategory category) {
        return Paths.get(fileDir, category.getFolder(), filename).toString();
    }

    // DB 저장용 상대경로 반환
    private String getRelativePath(String filename, FileCategory category) {
        return category.getFolder() + "/" + filename;
    }

    // 서버에 저장될 파일 이름 반환
    private String createSavedFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    // 파일 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) {
            return "";
        }
        return originalFilename.substring(pos + 1);
    }
}
