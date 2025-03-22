package com.davcatch.devcatch.service.tag;

import java.util.List;

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

	public Tag getByTageType(TagType tagType) throws CustomException {
		return tagRepository.findByTagType(tagType).orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
	}
}
