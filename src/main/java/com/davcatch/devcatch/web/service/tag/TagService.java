package com.davcatch.devcatch.web.service.tag;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.common.exception.ErrorCode;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.repository.tag.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

	private final TagRepository tagRepository;

	/**
	 * TagType 리스트 포함 Tag 찾기
	 * @param tagTypes 찾을 TagType List
	 * @return Tag List
	 */
	public List<Tag> getInTagTypes(List<TagType> tagTypes) {
		return tagRepository.findInTagType(tagTypes);
	}

	/**
	 * 전체 Tag 조회후 <TagType, Tag> Map으로 변환
	 * @return tag Map
	 */
	public Map<TagType, Tag> getAllTagsConvertMap() {
		return tagRepository.findAll().stream()
			.collect(Collectors.toMap(Tag::getTagType, tag -> tag));
	}
}
