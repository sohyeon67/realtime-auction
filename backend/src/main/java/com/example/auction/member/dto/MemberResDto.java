package com.example.auction.member.dto;

// 레코드 : 불변 객체를 쉽게 생성할 수 있음
// 자바 16부터 정식 스펙으로 포함
// 멤버 변수는 private final
// 모든 멤버 변수를 인자로 하는 public 생성자를 자동 생성. 이때, 기본생성자는 필요시 직접 생성해야 함
// 필드별 getter 자동 생성 => 필드명()로 호출
// equals, hashcode, toString 자동 생성
public record MemberResDto(String username, Boolean social, String name, String nickname, String phone) {
}
