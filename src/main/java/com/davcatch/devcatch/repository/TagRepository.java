package com.davcatch.devcatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;

public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByTagType(TagType tagType);

	@Query("select t from Tag t where t.tagType in :tagTypes")
	List<Tag> findInTagType(List<TagType> tagTypes);
}
