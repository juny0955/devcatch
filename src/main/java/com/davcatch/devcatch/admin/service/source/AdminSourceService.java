package com.davcatch.devcatch.admin.service.source;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.source.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminSourceService {

	private final SourceRepository sourceRepository;

	public List<Source> getSourceList() {
		return sourceRepository.findAll();
	}

	/**
	 * 새 소스 추가
	 * @param source 추가할 소스 정보
	 * @return 저장된 소스
	 */
	@Transactional
	public Source addSource(Source source) {
		log.info("새 소스 추가: {}", source.getName());
		return sourceRepository.save(source);
	}
}
