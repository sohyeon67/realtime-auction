package com.example.auction.auction.domain;

import com.example.auction.category.domain.Category;
import com.example.auction.member.domain.Member;
import com.example.auction.util.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auction extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @Column(nullable = false)
    private int startPrice;

    @Column(nullable = false)
    private int currentPrice;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @OneToMany(mappedBy = "auction")
    @Builder.Default
    private List<AuctionImage> images = new ArrayList<>();

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateEndTIme(LocalDateTime endTime) {
        if (this.status != AuctionStatus.READY) {
            throw new IllegalStateException("경매가 시작된 후에는 종료 시간을 변경할 수 없습니다.");
        }
        this.endTime = endTime;
    }

    public void updateStatus(AuctionStatus status) {
        this.status = status;
    }
}
