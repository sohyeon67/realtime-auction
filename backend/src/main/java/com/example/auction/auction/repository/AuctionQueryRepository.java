package com.example.auction.auction.repository;

import com.example.auction.auction.dto.AuctionListResDto;
import com.example.auction.auction.dto.AuctionSearchCond;
import com.example.auction.auction.dto.QAuctionListResDto;
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

    public Page<AuctionListResDto> auctionList(AuctionSearchCond cond, Pageable pageable) {
        List<AuctionListResDto> content = queryFactory
                .select(new QAuctionListResDto(
                        auction.id,
                        auctionImage.filePath,
                        auction.title,
                        auction.currentPrice,
                        bid.count().intValue(),
                        auction.endTime,
                        auction.status,
                        member.nickname
                ))
                .from(auction)
                .join(auction.seller, member)
                .leftJoin(auctionImage).on(auctionImage.auction.eq(auction).and(auctionImage.isMain.isTrue()))
                .leftJoin(bid).on(bid.auction.eq(auction))
                .where(
                        keywordContains(cond.getKeyword()),
                        inCategoryIds(cond.getCategoryIds()),
                        minPriceGoe(cond.getMinPrice()),
                        maxPriceLoe(cond.getMaxPrice())
                )
                .groupBy(auction.id, auctionImage.filePath, auction.title, auction.currentPrice, auction.endTime, auction.status, member.nickname)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(auction.count())
                .from(auction)
                .where(
                        keywordContains(cond.getKeyword()),
                        inCategoryIds(cond.getCategoryIds()),
                        minPriceGoe(cond.getMinPrice()),
                        maxPriceLoe(cond.getMaxPrice())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 검색 조건
    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? auction.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression inCategoryIds(List<Long> categoryIds) {
        return (categoryIds != null && !categoryIds.isEmpty()) ? auction.category.id.in(categoryIds) : null;
    }

    private BooleanExpression minPriceGoe(Integer minPrice) {
        return minPrice != null ? auction.currentPrice.goe(minPrice) : null;
    }

    private BooleanExpression maxPriceLoe(Integer maxPrice) {
        return maxPrice != null ? auction.currentPrice.loe(maxPrice) : null;
    }



}
