package com.davcatch.devcatch.repository.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.davcatch.devcatch.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	void deleteByEmail(String email);

	Optional<Member> findByEmail(String email);

	@EntityGraph(attributePaths = {"memberTags", "memberTags.tag"})
	@Query("select m from Member m")
	List<Member> findAllMemberWithTag();

	@EntityGraph(attributePaths = {"memberTags", "memberTags.tag"})
	@Query("select m from Member m where m.id = :memberId")
	Optional<Member> findMemberWithTag(Long memberId);

	@Query("select m from Member m order by m.createdAt desc")
	List<Member> findDashBoardList(Pageable pageable);

	@Query("select count(m.id) from Member m ")
	int findTotalMemberSize();
}
