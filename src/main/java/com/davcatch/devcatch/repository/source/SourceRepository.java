package com.davcatch.devcatch.repository.source;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.davcatch.devcatch.domain.source.Source;

public interface SourceRepository extends JpaRepository<Source, Long> {

	@Query("select s from Source s "
		+ "where s.isActive = true ")
	List<Source> findAllByIsActiveTrue();
}
