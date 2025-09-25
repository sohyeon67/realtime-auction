package com.example.auction.auction.service;

import com.example.auction.auction.domain.Auction;
import com.example.auction.auction.domain.AuctionStatus;
import com.example.auction.auction.dto.AuctionResDto;
import com.example.auction.auction.dto.AuctionSaveReqDto;
import com.example.auction.auction.dto.AuctionUpdateReqDto;
import com.example.auction.auction.repository.AuctionRepository;
import com.example.auction.category.domain.Category;
import com.example.auction.category.repository.CategoryRepository;
import com.example.auction.member.domain.Member;
import com.example.auction.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    public Long create(AuctionSaveReqDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        Auction auction = Auction.builder()
                .title(dto.getTitle())
                .category(category)
                .description(dto.getDescription())
                .seller(seller)
                .startPrice(dto.getStartPrice())
                .currentPrice(dto.getStartPrice())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(AuctionStatus.READY)
                .build();

        return auctionRepository.save(auction).getId();
    }

    @Transactional(readOnly = true)
    public AuctionResDto getAuction(Long id) {
        Auction auction = findAuctionOrThrow(id);
        return AuctionResDto.fromEntity(auction);
    }

    public void update(Long id, AuctionUpdateReqDto dto) {

        Auction auction = findAuctionOrThrow(id);

        // 본인만 수정 가능
        checkOwner(auction);

        if (dto.getTitle() != null) {
            auction.updateTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            auction.updateDescription(dto.getDescription());
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
            auction.updateCategory(category);
        }
        if (dto.getEndTime() != null) {
            auction.updateEndTIme(dto.getEndTime());
        }

    }

    public void delete(Long id) {
        Auction auction = findAuctionOrThrow(id);
        checkOwner(auction);
        checkStatus(auction, AuctionStatus.READY, "시작 전 경매만 삭제할 수 있습니다.");
        auctionRepository.delete(auction);
    }

    public void cancel(Long id) {
        Auction auction = findAuctionOrThrow(id);
        checkOwner(auction);
        checkStatus(auction, AuctionStatus.ONGOING, "진행 중인 경매만 취소할 수 있습니다.");
        auction.updateStatus(AuctionStatus.CANCELLED);
    }


    // 편의 메서드
    private Auction findAuctionOrThrow(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 정보를 찾을 수 없습니다."));
    }

    private static void checkOwner(Auction auction) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!auction.getSeller().getUsername().equals(username)) {
            throw new AccessDeniedException("본인만 수행할 수 있습니다.");
        }
    }

    private static void checkStatus(Auction auction, AuctionStatus ready, String s) {
        if (auction.getStatus() != ready) {
            throw new IllegalStateException(s);
        }
    }
}
