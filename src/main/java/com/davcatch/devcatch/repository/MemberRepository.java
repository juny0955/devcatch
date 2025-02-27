package com.davcatch.devcatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davcatch.devcatch.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);
}
