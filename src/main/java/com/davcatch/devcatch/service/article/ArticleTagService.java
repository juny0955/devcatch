package com.davcatch.devcatch.service.article;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.repository.article.ArticleTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleTagService {

	private final ArticleTagRepository articleTagRepository;


}
