package com.example.auction.auction.repository;

import com.example.auction.auction.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {


}
