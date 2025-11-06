package com.example.auction.auction.repository;

import com.example.auction.auction.domain.AuctionStatus;
import com.example.auction.auction.dto.AuctionListResDto;
import com.example.auction.auction.dto.AuctionSearchCond;
import com.example.auction.auction.dto.AuctionSort;
import com.example.auction.auction.dto.QAuctionListResDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.auction.auction.domain.QAuction.auction;
import static com.example.auction.auction.domain.QAuctionImage.auctionImage;
import static com.example.auction.bid.domain.QBid.bid;
import static com.example.auction.member.domain.QMember.member;

@Repository
public class AuctionQueryRepository {

    private final JPAQueryFactory queryFactory;

    public AuctionQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<AuctionListResDto> auctionList(AuctionSearchCond cond, Pageable pageable, AuctionSort sort) {

        List<AuctionListResDto> content = queryFactory
                .select(new QAuctionListResDto(
                        auction.id,
                        auctionImage.filePath,
                        auction.title,
                        auction.currentPrice,
                        auction.bidCount,
                        auction.endTime,
                        auction.status,
                        member.nickname
                ))
                .from(auction)
                .join(auction.seller, member)
                .leftJoin(auctionImage).on(auctionImage.auction.eq(auction).and(auctionImage.isMain.isTrue()))
                .where(
                        keywordContains(cond.getKeyword()),
                        sellerNicknameContains(cond.getSellerName()),
                        inCategoryIds(cond.getCategoryIds()),
                        minPriceGoe(cond.getMinPrice()),
                        maxPriceLoe(cond.getMaxPrice()),
                        eqStatus(cond.getStatus())
                )
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(auction.count())
                .from(auction)
                .join(auction.seller, member)
                .where(
                        keywordContains(cond.getKeyword()),
                        sellerNicknameContains(cond.getSellerName()),
                        inCategoryIds(cond.getCategoryIds()),
                        minPriceGoe(cond.getMinPrice()),
                        maxPriceLoe(cond.getMaxPrice()),
                        eqStatus(cond.getStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 검색 조건
    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? auction.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression sellerNicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? member.nickname.containsIgnoreCase(nickname) : null;
    }

    private BooleanExpression inCategoryIds(List<Long> categoryIds) {
        return (categoryIds != null && !categoryIds.isEmpty()) ? auction.category.id.in(categoryIds) : null;
    }

    private BooleanExpression minPriceGoe(Long minPrice) {
        return minPrice != null ? auction.currentPrice.goe(minPrice) : null;
    }

    private BooleanExpression maxPriceLoe(Long maxPrice) {
        return maxPrice != null ? auction.currentPrice.loe(maxPrice) : null;
    }

    private BooleanExpression eqStatus(AuctionStatus status) {
        return status != null ? auction.status.eq(status) : null;
    }

    // 정렬
    private OrderSpecifier<?> getOrderSpecifier(AuctionSort sort) {
        switch (sort) {
            case POPULARITY:
                return auction.bidCount.desc();
            case ENDING_SOON: // 나중에 보충 필요
                return auction.endTime.asc();
            case RECENT:
                return auction.createdAt.desc();
            case PRICE_DESC:
                return auction.currentPrice.desc();
            case PRICE_ASC:
                return auction.currentPrice.asc();
            default:
                return auction.bidCount.desc();
        }
    }

}
