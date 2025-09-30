package com.example.auction.file.service;

import com.example.auction.exception.FileStorageException;
import com.example.auction.file.domain.FileCategory;
import com.example.auction.file.dto.UploadFile;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class LocalFileUploadService implements FileUploadService {

    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.base-url}")
    private String baseUrl;

    @Override
    public UploadFile upload(MultipartFile file, FileCategory category) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String savedFilename = createSavedFilename(originalFilename);
            String relativePath = getRelativePath(savedFilename, category);
            String fullPath = getFullPath(savedFilename, category);

            createDirectoryIfNotExists(category);

            file.transferTo(new File(fullPath)); // 업로드

            return new UploadFile(originalFilename, savedFilename, relativePath);
        } catch (IOException e) {
            throw new FileStorageException("파일 업로드 중 오류가 발생했습니다.", e);
        }

    }

    @Override
    public List<UploadFile> uploadAll(List<MultipartFile> files, FileCategory category) {
        List<UploadFile> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                savedFiles.add(upload(file, category));
            }
        }

        return savedFiles;
    }

    @Override
    public void delete(String relativePath) {
        try {
            Path fullPath = Paths.get(fileDir, relativePath);
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.warn("파일 삭제 실패 : {}", relativePath, e);
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
