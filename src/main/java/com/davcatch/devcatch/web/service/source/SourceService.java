package com.davcatch.devcatch.web.service.source;

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
public class SourceService {

	private final SourceRepository sourceRepository;

	/**
	 * 활성화된 소스 조회
	 * @return 활성화 상태 소스 리스트
	 */
	public List<Source> getActiveSources() {
		return sourceRepository.findAllByIsActiveTrue();
	}
}
