package com.example.auction.member.service;

import com.example.auction.jwt.service.JwtService;
import com.example.auction.member.domain.Member;
import com.example.auction.member.domain.MemberStatus;
import com.example.auction.member.domain.RoleType;
import com.example.auction.member.domain.SocialProviderType;
import com.example.auction.member.dto.*;
import com.example.auction.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService extends DefaultOAuth2UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    // 자체 회원가입(존재 여부)
    @Transactional(readOnly = true)
    public Boolean existMember(MemberExistReqDto dto) {
        return memberRepository.existsByUsername(dto.getUsername());
    }

    // 자체 회원가입
    @Transactional
    public Long signup(MemberSaveReqDto dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }

        Member member = Member.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword())) // 암호화
                .isSocial(false)
                .name(dto.getName())
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .role(RoleType.USER)
                .status(MemberStatus.ACTIVE)
                .build();

        return memberRepository.save(member).getId();
    }

    // 자체 로그인. AuthenticationManager를 통한 인증이 필요할 때 사용
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member entity = memberRepository.findByUsernameAndStatusAndIsSocial(username, MemberStatus.ACTIVE, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRole().name())
                .build();
    }

    // 내 정보
    @Transactional(readOnly = true)
    public MemberResDto getMyInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByUsernameAndStatus(username, MemberStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new MemberResDto(member.getUsername(), false, member.getName(), member.getNickname(), member.getPhone());
    }

    // 자체 로그인 회원 정보 수정
    @Transactional
    public Long updateMember(MemberUpdateReqDto dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(dto.getUsername())) {
            throw new AccessDeniedException("본인 계정만 수정 가능합니다.");
        }

        Member member = memberRepository.findByUsernameAndStatusAndIsSocial(dto.getUsername(), MemberStatus.ACTIVE, false)
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));

        member.modifyInfo(dto); // Dirty Checking

        return member.getId();
    }

    // 자체/소셜 로그인 회원 탈퇴
    @Transactional
    public void deleteMember(String username) {
        // 본인 및 어드민만 삭제 가능
        SecurityContext context = SecurityContextHolder.getContext();
        String currentUsername = context.getAuthentication().getName();
        String currentRole = context.getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner = currentUsername.equals(username);
        boolean isAdmin = currentRole.equals(RoleType.ADMIN.name());

        if (!isOwner && isAdmin) {
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        // 유저 탈퇴 처리
        Member member = memberRepository.findByUsernameAndStatusAndIsSocial(username, MemberStatus.ACTIVE, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        member.withdraw();

        // 리프레시 토큰 제거
        jwtService.removeRefreshUser(username);
    }

    // 소셜 로그인
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes;
        List<GrantedAuthority> authorities;

        String username;
        String role = RoleType.USER.name();
        String name;
        String phone = "010-0000-0000";

        // 제공자별 데이터 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (registrationId.equals(SocialProviderType.NAVER.name())) {
            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            username = registrationId + "_" + attributes.get("id");
            name = attributes.get("name").toString();
            log.info("네이버, {}", attributes);
        } else if (registrationId.equals(SocialProviderType.GOOGLE.name())) {
            attributes = (Map<String, Object>) oAuth2User.getAttributes();
            username = registrationId + "_" + attributes.get("sub");
            name = attributes.get("name").toString();
            log.info("구글, {}", attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // DB에 없으면 신규 저장, 있으면 업데이트
        // .........
        Optional<Member> entity = memberRepository.findByUsername(username);
        if (entity.isPresent()) { // 업데이트
            role = entity.get().getRole().name();

            // 기존 유저 업데이트

        } else { // 신규 유저 추가
            Member newMember = Member.builder()
                    .username(username)
                    .password("")
                    .name(name)
                    .nickname(name)
                    .phone(phone)
                    .role(RoleType.USER)
                    .isSocial(true)
                    .socialProviderType(SocialProviderType.valueOf(registrationId))
                    .status(MemberStatus.TEMP)
                    .build();

            memberRepository.save(newMember);
        }


        // OAuth2LoginAuthenticationFilter 필터가 OAuth2UserService가 반환한 OAuth2User를 받아서
        // SecurityContextHolder에 Authentication 객체로 세팅해준다.
        // 성공 핸들러에서 authentication 객체에 정보들이 담겨있다.
        authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomOAuth2User(attributes, authorities, username);
    }

}
