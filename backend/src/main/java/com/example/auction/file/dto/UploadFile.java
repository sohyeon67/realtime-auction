package com.example.auction.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadFile {

    private String originalName;
    private String savedName;
    private String filePath;

}
