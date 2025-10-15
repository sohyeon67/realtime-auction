package com.example.auction.auction.domain;

import com.example.auction.util.BaseCreated;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuctionImage extends BaseCreated {

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

    public void updateSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void updateIsMain(Boolean isMain) {
        this.isMain = isMain;
    }
}
