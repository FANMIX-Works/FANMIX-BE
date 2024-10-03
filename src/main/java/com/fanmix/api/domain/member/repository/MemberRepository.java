package com.fanmix.api.domain.member.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fanmix.api.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	Optional<Member> findByName(String name);

	Optional<Member> findByNickName(String nickName);

	Optional<Member> findByRefreshToken(String refreshToken);

	Optional<Member> findById(int id);

	Page<Member> findAll(Pageable pageable);
}
