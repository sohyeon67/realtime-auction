package com.example.auction.auction.domain;

import com.example.auction.category.domain.Category;
import com.example.auction.member.domain.Member;
import com.example.auction.util.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @Column(nullable = false)
    private Long startPrice;

    @Column(nullable = false)
    private Long currentPrice;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(nullable = false)
    @Builder.Default
    private int bidCount = 0; // 반정규화

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

    public void updateStartPrice(Long startPrice) {
        this.startPrice = startPrice;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateCurrentPrice(Long currentPrice) {
        this.currentPrice = currentPrice;
    }

    /// 비즈니스
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    public boolean isBeforeStart() {
        return LocalDateTime.now().isBefore(startTime);
    }

    public void cancel() {
        if (!isOngoing()) {
            throw new IllegalStateException("진행 중인 경매만 취소할 수 있습니다.");
        }
        this.status = AuctionStatus.CANCELLED;
    }

    public void checkModifiable() {
        if (!isBeforeStart()) {
            throw new IllegalStateException("경매 시작 후에는 수정하거나 삭제할 수 없습니다.");
        }
    }

    public void increaseBidCount() {
        this.bidCount++;
    }


}
