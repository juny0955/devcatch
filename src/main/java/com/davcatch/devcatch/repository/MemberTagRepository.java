package com.davcatch.devcatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davcatch.devcatch.domain.MemberTag;

public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
}
