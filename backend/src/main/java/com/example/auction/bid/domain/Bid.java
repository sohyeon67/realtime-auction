package com.example.auction.bid.domain;

import com.example.auction.auction.domain.Auction;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Bid {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

}
