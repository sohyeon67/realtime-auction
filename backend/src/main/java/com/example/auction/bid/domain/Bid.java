package com.example.auction.bid.domain;

import com.example.auction.auction.domain.Auction;
import com.example.auction.member.domain.Member;
import com.example.auction.util.BaseCreated;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bid extends BaseCreated {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Long bidPrice;

}
