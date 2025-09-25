package com.example.auction.auction.domain;

public enum AuctionStatus {

    READY,  // 등록 후 대기
    ONGOING, // 경매 진행중
    SOLD, // 낙찰 완료
    CANCELLED, // 취소
    EXPIRED // 기간 만료
}
