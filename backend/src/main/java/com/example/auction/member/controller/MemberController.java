package com.example.auction.member.controller;

import com.example.auction.member.dto.*;
import com.example.auction.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 자체 로그인 유저 존재 확인
    @PostMapping("/exist")
    public ResponseEntity<Boolean> existMember(@RequestBody @Valid MemberExistReqDto dto) {
        return ResponseEntity.ok(memberService.existMember(dto));
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<MemberIdResDto> signup(@RequestBody @Valid MemberSaveReqDto dto) {
        Long id = memberService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MemberIdResDto(id));
    }

    // 내 정보
    @GetMapping("/me")
    public ResponseEntity<MemberResDto> getMyInfo() {
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    // 정보 수정
    @PutMapping("/me")
    public ResponseEntity<MemberIdResDto> updateMember(@RequestBody @Valid MemberUpdateReqDto dto) {
        Long id = memberService.updateMember(dto);
        return ResponseEntity.ok(new MemberIdResDto(id));
    }

    // 탈퇴
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteMember(@PathVariable String username) {
        memberService.deleteMember(username);
        return ResponseEntity.noContent().build();
    }

}
