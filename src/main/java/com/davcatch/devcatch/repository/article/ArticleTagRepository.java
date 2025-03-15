package com.davcatch.devcatch.repository.article;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davcatch.devcatch.domain.ArticleTag;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
}
