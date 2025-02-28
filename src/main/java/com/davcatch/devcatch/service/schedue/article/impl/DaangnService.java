package com.davcatch.devcatch.service.schedue.article.impl;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.integration.rss.RssReader;
import com.davcatch.devcatch.repository.ArticleRepository;
import com.davcatch.devcatch.repository.SourceRepository;
import com.davcatch.devcatch.service.schedue.article.AbstractArticleScheduler;
import com.davcatch.devcatch.service.schedue.article.ArticleFactory;

@Service
public class DaangnService extends AbstractArticleScheduler {

	public DaangnService(RssReader rssReader, SourceRepository sourceRepository, ArticleRepository articleRepository,
		ArticleFactory articleFactory) {
		super(rssReader, sourceRepository, articleRepository, articleFactory);
	}

	@Override
	protected String getSourceName() {
		return "daangn";
	}
}
