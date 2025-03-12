package com.davcatch.devcatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davcatch.devcatch.domain.Tag;
import com.davcatch.devcatch.domain.TagType;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByTagType(TagType tagType);
}
