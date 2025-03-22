package com.davcatch.devcatch.service.admin.article;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.repository.article.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminArticleService {

	private final ArticleRepository articleRepository;

	public List<Article> getDashBoardArticleList() {
		return articleRepository.findDashboardList(PageRequest.of(0, 5));
	}
}
