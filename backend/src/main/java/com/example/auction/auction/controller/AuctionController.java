package com.example.auction.auction.controller;

import com.example.auction.auction.dto.AuctionIdResDto;
import com.example.auction.auction.dto.AuctionResDto;
import com.example.auction.auction.dto.AuctionSaveReqDto;
import com.example.auction.auction.dto.AuctionUpdateReqDto;
import com.example.auction.auction.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<AuctionIdResDto> create(@Valid AuctionSaveReqDto dto) throws IOException {
        Long id = auctionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuctionIdResDto(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResDto> getOne(@PathVariable Long id) {
        AuctionResDto dto = auctionService.getAuction(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuctionIdResDto> update(@PathVariable Long id, @RequestBody AuctionUpdateReqDto dto) {
        auctionService.update(id, dto);
        return ResponseEntity.ok(new AuctionIdResDto(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auctionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        auctionService.cancel(id);
        return ResponseEntity.ok().build();
    }

//    @GetMapping
//    public ResponseEntity<?> getAll() {
//        return ResponseEntity.ok("temp");
//    }
}
