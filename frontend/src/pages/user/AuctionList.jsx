export default function AuctionList() {
  return (
    <div style={{ display: "flex" }}>
      <aside style={{ width: "250px", borderRight: "1px solid #ddd", padding: "1rem" }}>
        <h3>검색 / 필터</h3>
        {/* 검색 조건 UI */}
      </aside>
      <section style={{ flex: 1, padding: "1rem" }}>
        <h2>경매 목록</h2>
        {/* 경매 카드 리스트 */}
      </section>
    </div>
  );
}