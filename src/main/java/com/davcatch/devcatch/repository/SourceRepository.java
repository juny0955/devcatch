package com.davcatch.devcatch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davcatch.devcatch.domain.Source;

public interface SourceRepository extends JpaRepository<Source, Long> {

	Optional<Source> findByName(String name);
}
