package com.davcatch.devcatch.service.admin.source;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.source.SourceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSourceService {

	private final SourceRepository sourceRepository;

	public List<Source> getSourceList() {
		return sourceRepository.findAll();
	}
}
