package com.example.auction.member.domain;

import com.example.auction.member.dto.MemberUpdateReqDto;
import com.example.auction.util.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private Boolean isSocial;

    @Enumerated(EnumType.STRING)
    private SocialProviderType socialProviderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    // 필요시 수정용 메서드 따로 만들기

    // 정보 수정
    public void modifyInfo(MemberUpdateReqDto dto) {
        this.name = dto.getName();
        this.nickname = dto.getNickname();
        this.phone = dto.getPhone();
    }

    // 탈퇴
    public void withdraw() {
        this.status = MemberStatus.DELETED;
    }
}
