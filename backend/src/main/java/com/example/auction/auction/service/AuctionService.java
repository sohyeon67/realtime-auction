package com.example.auction.auction.service;

import com.example.auction.auction.domain.Auction;
import com.example.auction.auction.domain.AuctionImage;
import com.example.auction.auction.domain.AuctionStatus;
import com.example.auction.auction.dto.*;
import com.example.auction.auction.repository.AuctionImageRepository;
import com.example.auction.auction.repository.AuctionQueryRepository;
import com.example.auction.auction.repository.AuctionRepository;
import com.example.auction.category.domain.Category;
import com.example.auction.category.repository.CategoryRepository;
import com.example.auction.file.domain.FileCategory;
import com.example.auction.file.dto.UploadFile;
import com.example.auction.file.service.FileUploadService;
import com.example.auction.member.domain.Member;
import com.example.auction.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService {

    private final FileUploadService fileUploadService;

    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final AuctionQueryRepository auctionQueryRepository;

    public Long create(AuctionSaveReqDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // 경매 생성
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
        auctionRepository.save(auction);

        // 이미지 업로드
        for (AuctionImageReqDto fileDto : dto.getFiles()) {
            // 업로드
            MultipartFile file = fileDto.getFile();
            UploadFile upload = fileUploadService.upload(file, FileCategory.AUCTION);

            // 엔티티 생성
            AuctionImage auctionImage = AuctionImage.builder()
                    .auction(auction)
                    .originalName(upload.getOriginalName())
                    .savedName(upload.getSavedName())
                    .filePath(upload.getFilePath())
                    .sortOrder(fileDto.getSortOrder())
                    .isMain(fileDto.getIsMain())
                    .build();
            auctionImageRepository.save(auctionImage);
        }

        return auction.getId();
    }

    @Transactional(readOnly = true)
    public AuctionDetailResDto getAuction(Long id) {
        Auction auction = findAuctionOrThrow(id);

        // 이미지 URL 변환
        List<AuctionImageResDto> images = auction.getImages().stream()
                .map(img -> AuctionImageResDto.builder()
                        .id(img.getId())
                        .url(fileUploadService.getFileUrl(img.getFilePath())) // 상대경로 -> URL
                        .sortOrder(img.getSortOrder())
                        .isMain(img.isMain())
                        .build()
                ).toList();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean canEdit = auction.getSeller().getUsername().equals(username);

        return AuctionDetailResDto.builder()
                .auctionId(auction.getId())
                .title(auction.getTitle())
                .categoryId(auction.getCategory().getId())
                .categoryName(auction.getCategory().getName())
                .description(auction.getDescription())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .canEdit(canEdit)
                .images(images)
                .build();
    }

    public void update(Long id, AuctionUpdateReqDto dto) {

        Auction auction = findAuctionOrThrow(id);

        // 본인만 수정 가능
        checkOwner(auction);

        // 기존 시작시간을 가져와 경매 시작 전인지 체크
        auction.checkModifiable();

        // 필드 수정
        if (dto.getTitle() != null) {
            auction.updateTitle(dto.getTitle());
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
            auction.updateCategory(category);
        }
        if (dto.getDescription() != null) {
            auction.updateDescription(dto.getDescription());
        }
        if (dto.getStartPrice() != null) {
            auction.updateStartPrice(dto.getStartPrice());
        }
        if (dto.getStartTime() != null) {
            auction.updateStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            auction.updateEndTime(dto.getEndTime());
        }


        // 기존 이미지 파일 & 목록에 없으면 삭제
        for (AuctionImage image : auction.getImages()) {
            // existFiles에서 동일한 id 찾기
            AuctionImageReqDto matchedDto = dto.getExistFiles().stream()
                    .filter(f -> f.getId() != null && f.getId().equals(image.getId()))
                    .findFirst()
                    .orElse(null);

            if (matchedDto == null) {
                // 존재하지 않으면 삭제
                fileUploadService.delete(image.getFilePath());
                auctionImageRepository.delete(image);
            } else {
                // 존재하면 sortOrder / isMain 갱신
                image.updateSortOrder(matchedDto.getSortOrder());
                image.updateIsMain(matchedDto.getIsMain());
            }
        }


        // 새 이미지 업로드 처리
        for (AuctionImageReqDto fileDto : dto.getNewFiles()) {
            // 업로드
            MultipartFile file = fileDto.getFile();
            UploadFile upload = fileUploadService.upload(file, FileCategory.AUCTION);

            // 엔티티 생성
            AuctionImage auctionImage = AuctionImage.builder()
                    .auction(auction)
                    .originalName(upload.getOriginalName())
                    .savedName(upload.getSavedName())
                    .filePath(upload.getFilePath())
                    .sortOrder(fileDto.getSortOrder())
                    .isMain(fileDto.getIsMain())
                    .build();
            auctionImageRepository.save(auctionImage);
        }

    }

    // 경매 시작 전 삭제
    public void delete(Long id) {
        Auction auction = findAuctionOrThrow(id);
        checkOwner(auction);
        auction.checkModifiable();

        // 경매 이미지 삭제
        List<AuctionImage> images = auction.getImages();
        for (AuctionImage image : images) {
            fileUploadService.delete(image.getFilePath()); // 파일 삭제
            auctionImageRepository.delete(image); // DB 삭제
        }

        // 경매 삭제
        auctionRepository.delete(auction);
    }

    // 경매 시작 이후 취소
    public void cancel(Long id) {
        Auction auction = findAuctionOrThrow(id);
        checkOwner(auction);
        auction.cancel();
    }

    // 경매 목록 조회
    public Page<AuctionListResDto> getAuctions(AuctionSearchCond cond, Pageable pageable, AuctionSort sort) {
        return auctionQueryRepository.auctionList(cond, pageable, sort)
                .map(auction -> {
                    if (auction.getMainImageUrl() != null) {
                        auction.setMainImageUrl(fileUploadService.getFileUrl(auction.getMainImageUrl()));
                    }
                    return auction;
                });
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


}
