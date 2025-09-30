package com.example.auction.file.service;

import com.example.auction.file.domain.FileCategory;
import com.example.auction.file.dto.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {

    UploadFile upload(MultipartFile file, FileCategory category);
    List<UploadFile> uploadAll(List<MultipartFile> files, FileCategory category);
    void delete(String relativePath);

    /**
     * 프론트에서 접근할 수 있는 파일 경로 반환
     * @param relativePath 상대경로
     * @return 파일 URL 경로
     */
    String getFileUrl(String relativePath);
}
