// 경매 상태 상수
export const AUCTION_STATUS = Object.freeze({
  READY: 'READY',
  ONGOING: 'ONGOING',
  SOLD: 'SOLD',
  CANCELLED: 'CANCELLED',
  EXPIRED: 'EXPIRED',
});

// 상태별 메타 정보
export const AUCTION_STATUS_META = Object.freeze({
  [AUCTION_STATUS.READY]: { text: '시작전', color: 'orange' },
  [AUCTION_STATUS.ONGOING]: { text: '진행중', color: 'green' },
  [AUCTION_STATUS.SOLD]: { text: '낙찰완료', color: 'blue' },
  [AUCTION_STATUS.CANCELLED]: { text: '취소', color: 'gray' },
  [AUCTION_STATUS.EXPIRED]: { text: '기간만료', color: 'red' },
});

// 검색 필터 옵션
export const AUCTION_STATUS_OPTIONS = Object.values(AUCTION_STATUS).map(status => ({
  key: status,
  text: AUCTION_STATUS_META[status].text,
}));
