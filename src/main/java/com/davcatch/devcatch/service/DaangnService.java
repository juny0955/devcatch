package com.davcatch.devcatch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.Article;
import com.davcatch.devcatch.domain.Source;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.gpt.GptSummaryService;
import com.davcatch.devcatch.gpt.response.GptResponse;
import com.davcatch.devcatch.repository.ArticleRepository;
import com.davcatch.devcatch.repository.SourceRepository;
import com.davcatch.devcatch.rss.RssReader;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DaangnService {

	private final RssReader rssReader;
	private final SourceRepository sourceRepository;
	private final ArticleRepository articleRepository;
	private final GptSummaryService gptSummaryService;

	/**
	 * 당근 Article 생성
	 */
	public void createNewArticle() throws CustomException {
		log.info("당근 ");
		Source source = sourceRepository.findByName("daangn")
			.orElseThrow(() -> new CustomException(ErrorCode.SOURCE_NOT_FOUND));

		// rss feed 내용 가져오기
		SyndFeed feed = rssReader.reader(source.getFeedUrl());

		// published_at이 마지막 Article 불러오기
		Optional<Article> lastPublishedArticle = articleRepository.findLastPublishedArticle(source.getId());

		List<Article> articles = new ArrayList<>();

		// 이전 데이터 없을 경우 전체 저장
		if (lastPublishedArticle.isEmpty()) {
			for (SyndEntry entry : feed.getEntries())
				articles.add(getSummaryAndAddList(entry, source));
		} else {
				/*
				이전 데이터 있을 경우
				lastPublishedArticle 이후 데이터만 필터링
				GPT 요약 요청 -> Article 생성 후 List에 추가
				 */
			for (SyndEntry entry : feed.getEntries()) {
				if (entry.getPublishedDate().after(lastPublishedArticle.get().getPublishedAt()))
					articles.add(getSummaryAndAddList(entry, source));
			}
		}

		if (!articles.isEmpty())
			articleRepository.saveAll(articles);
	}

	/**
	 * Article 생성
	 *
	 * TODO Factory 클래스로 분리 예정
	 * @param entry Rss Feed
	 * @param source 해당 source
	 * @return Article
	 */
	private Article getSummaryAndAddList(SyndEntry entry, Source source) throws CustomException {
		GptResponse response = gptSummaryService.getSummary(entry.getContents().get(0).getValue());

		return Article.from(source, entry, response);
	}
}
