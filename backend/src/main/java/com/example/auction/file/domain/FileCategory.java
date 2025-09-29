package com.example.auction.file.domain;

import lombok.Getter;

@Getter
public enum FileCategory {

    AUCTION("auction");

    private final String folder;

    FileCategory(String folder) {
        this.folder = folder;
    }
}
