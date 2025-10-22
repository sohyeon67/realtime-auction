package com.example.auction.bid.repository;

import com.example.auction.bid.domain.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {

    Page<Bid> findByAuctionIdOrderByCreatedAtDesc(Long auctionId, Pageable pageable);
}
