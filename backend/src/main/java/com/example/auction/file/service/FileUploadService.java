package com.example.auction.file.service;

import com.example.auction.file.dto.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {

    UploadFile upload(MultipartFile file) throws IOException;
    List<UploadFile> uploadAll(List<MultipartFile> files) throws IOException;
    void delete(String key) throws IOException;
}
