package com.example.auction.member.repository;

import com.example.auction.member.domain.Member;
import com.example.auction.member.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);

    Optional<Member> findByUsernameAndStatus(String username, MemberStatus memberStatus);

    Optional<Member> findByUsernameAndStatusAndIsSocial(String username, MemberStatus status, Boolean isSocial);
}
