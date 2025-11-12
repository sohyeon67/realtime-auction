package com.example.auction.auction.repository;

import com.example.auction.auction.domain.Auction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

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

    /**
     * 일반 조회용
     */
    @Query("SELECT a FROM Auction a JOIN FETCH a.seller WHERE a.id = :id")
    Optional<Auction> findByIdWithSeller(Long id);

    /**
     * 입찰용 (비관적락)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 쓰기 락
    @Query("SELECT a FROM Auction a JOIN FETCH a.seller WHERE a.id = :id")
    Optional<Auction> findByIdWithSellerForUpdate(@Param("id") Long id);
}
