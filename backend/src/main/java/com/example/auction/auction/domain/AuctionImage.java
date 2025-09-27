package com.example.auction.auction.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String savedName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private boolean isMain;

    @Column(nullable = false)
    private int sortOrder;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
