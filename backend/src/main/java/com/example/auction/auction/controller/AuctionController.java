package com.example.auction.auction.controller;

import com.example.auction.auction.dto.*;
import com.example.auction.auction.service.AuctionService;
import com.example.auction.bid.dto.BidCursorResDto;
import com.example.auction.bid.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;

    @PostMapping
    public ResponseEntity<AuctionIdResDto> create(@Valid @ModelAttribute AuctionSaveReqDto dto) {
        Long id = auctionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuctionIdResDto(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionDetailResDto> getOne(@PathVariable Long id) {
        AuctionDetailResDto dto = auctionService.getAuction(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuctionIdResDto> update(@PathVariable Long id, AuctionUpdateReqDto dto) {
        auctionService.update(id, dto);
        return ResponseEntity.ok(new AuctionIdResDto(id));
    }

    // READY 상태의 경매만 삭제 가능
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auctionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ONGOING 상태의 경매만 취소 가능
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        auctionService.cancel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AuctionListResDto>> getAll(
            AuctionSearchCond cond,
            @PageableDefault(size = 12) Pageable pageable,
            @RequestParam(required = false, defaultValue = "POPULARITY") AuctionSort sort
    ) {
        return ResponseEntity.ok(auctionService.getAuctions(cond, pageable, sort));
    }

    // 특정 경매의 입찰 목록 조회
    @GetMapping("/{auctionId}/bids")
    public BidCursorResDto getBidsByAuction(@PathVariable Long auctionId,
                                            @RequestParam(required = false) Long lastBidId,
                                            @RequestParam(defaultValue = "10") int size
    ) {
        return bidService.findBidsByAuctionId(auctionId, lastBidId, size);
    }
}
