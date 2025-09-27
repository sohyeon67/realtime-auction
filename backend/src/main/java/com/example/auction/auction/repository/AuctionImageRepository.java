package com.example.auction.auction.repository;

import com.example.auction.auction.domain.AuctionImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long> {
}
