package com.example.auction.bid.repository;

import com.example.auction.bid.domain.Bid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    @Query("""
            SELECT b FROM Bid b 
            WHERE b.auction.id = :auctionId 
            AND (:lastBidId IS NULL OR b.id < :lastBidId) 
            ORDER BY b.id DESC
    """)
    List<Bid> findNextBidsByBidId(@Param("auctionId") Long auctionId, @Param("lastBidId") Long lastBidId, Pageable pageable);
}
