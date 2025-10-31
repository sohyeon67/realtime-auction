export const AUCTION_SORT = Object.freeze({
  POPULARITY: 'POPULARITY',
  ENDING_SOON: 'ENDING_SOON',
  RECENT: 'RECENT',
  PRICE_DESC: 'PRICE_DESC',
  PRICE_ASC: 'PRICE_ASC',
});

export const AUCTION_SORT_OPTIONS = [
  { key: AUCTION_SORT.POPULARITY, text: '인기경매순' },
  { key: AUCTION_SORT.ENDING_SOON, text: '마감임박순' },
  { key: AUCTION_SORT.RECENT, text: '신규경매순' },
  { key: AUCTION_SORT.PRICE_DESC, text: '높은가격순' },
  { key: AUCTION_SORT.PRICE_ASC, text: '낮은가격순' },
];
