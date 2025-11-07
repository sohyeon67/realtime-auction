package com.example.auction.auction.repository;

import com.example.auction.auction.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Modifying
    @Query("""
        UPDATE Auction a
           SET a.status = 'ONGOING'
         WHERE a.startTime <= :now
           AND a.status = 'READY'
    """)
    int startReadyAuctions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("""
        UPDATE Auction a
           SET a.status = CASE
                            WHEN EXISTS (
                                SELECT 1 FROM Bid b WHERE b.auction = a
                            ) THEN 'SOLD'
                              ELSE 'EXPIRED'
                          END
         WHERE a.endTime <= :now
           AND a.status = 'ONGOING'
    """)
    int endOngoingAuctions(@Param("now") LocalDateTime now);


}
