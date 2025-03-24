package com.davcatch.devcatch.schedule.article.parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davcatch.devcatch.common.exception.CustomException;
import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.ParseMethod;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.article.ArticleRepository;
import com.davcatch.devcatch.schedule.article.dto.ParsedArticle;
import com.davcatch.devcatch.schedule.article.parser.factory.ArticleParseStrategyFactory;
import com.davcatch.devcatch.schedule.article.parser.strategy.ArticleParseStrategy;

@ExtendWith(MockitoExtension.class)
class ArticleParseServiceTest {

	@Mock
	private ArticleParseStrategyFactory strategyFactory;

	@Mock
	private ArticleRepository articleRepository;

	@Mock
	private ArticleParseStrategy articleParseStrategy;

	@InjectMocks
	private ArticleParseService articleParseService;

	private Source source = mock(Source.class);
	private List<ParsedArticle> parsedArticles;

	@BeforeEach
	void init() {
		when(source.getId()).thenReturn(1L);
		when(source.getName()).thenReturn("Test");
		when(source.getMainUrl()).thenReturn("test.com");
		when(source.getFeedUrl()).thenReturn("test.com/feed");
		when(source.getParseMethod()).thenReturn(ParseMethod.RSS_CONTENT);
		when(source.isActive()).thenReturn(true);
		when(source.isUseLink()).thenReturn(true);

		parsedArticles = Arrays.asList(
			createParsedArticle("Article 1", "https://testblog.com/article1", new Date()),
			createParsedArticle("Article 2", "https://testblog.com/article2", new Date()),
			createParsedArticle("Article 3", "https://testblog.com/article3", new Date())
		);
	}

	@Test
	@DisplayName("아티클_파싱_성공")
	void parseArticle_Success() throws CustomException {
		when(strategyFactory.getStrategy(any(ParseMethod.class))).thenReturn(articleParseStrategy);
		when(articleParseStrategy.process(source)).thenReturn(parsedArticles);
		when(articleRepository.findLastPublishedArticle(source.getId())).thenReturn(Optional.empty());
		when(articleRepository.findExistsLinks(anySet())).thenReturn(new HashSet<>());

		List<ParsedArticle> result = articleParseService.parseArticles(source);

		verify(strategyFactory).getStrategy(source.getParseMethod());
		verify(articleParseStrategy).process(source);
		verify(articleRepository).findLastPublishedArticle(source.getId());
		verify(articleRepository).findExistsLinks(anySet());

		assertEquals(3, result.size());
	}

	@Test
	@DisplayName("중복_링크_필터링")
	void parseArticles_FilterExistingLinks() throws CustomException {
		when(strategyFactory.getStrategy(any(ParseMethod.class))).thenReturn(articleParseStrategy);
		when(articleParseStrategy.process(source)).thenReturn(parsedArticles);
		when(articleRepository.findLastPublishedArticle(source.getId())).thenReturn(Optional.empty());

		// 두 번째 아티클이 이미 존재한다고 가정
		Set<String> existingLinks = new HashSet<>();
		existingLinks.add("https://testblog.com/article2");
		when(articleRepository.findExistsLinks(anySet())).thenReturn(existingLinks);

		List<ParsedArticle> result = articleParseService.parseArticles(source);

		verify(articleRepository).findExistsLinks(anySet());

		assertEquals(2, result.size());
		assertFalse(result.stream().anyMatch(article -> article.getLink().equals("https://testblog.com/article2")));
	}

	@Test
	@DisplayName("마지막_발행날짜_이후_필터링")
	void parseArticles_FilterByLastPublishedDate() throws CustomException {
		when(strategyFactory.getStrategy(any(ParseMethod.class))).thenReturn(articleParseStrategy);

		Date yesterday = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

		List<ParsedArticle> mixedDateArticles = Arrays.asList(
			createParsedArticle("Old Article", "https://testblog.com/old", yesterday),
			createParsedArticle("New Article", "https://testblog.com/new", tomorrow)
		);

		when(articleParseStrategy.process(source)).thenReturn(mixedDateArticles);

		Article lastArticle = mock(Article.class);
		Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		when(lastArticle.getPublishedAt()).thenReturn(today);
		when(articleRepository.findLastPublishedArticle(source.getId())).thenReturn(Optional.of(lastArticle));

		when(articleRepository.findExistsLinks(anySet())).thenReturn(new HashSet<>());

		List<ParsedArticle> result = articleParseService.parseArticles(source);

		verify(articleRepository).findLastPublishedArticle(source.getId());

		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("파싱_전략_찾기_실패")
	void parseArticles_StrategyFactoryFails() throws CustomException {
		// Given
		when(strategyFactory.getStrategy(any(ParseMethod.class))).thenThrow(CustomException.class);

		// When & Then
		assertThrows(CustomException.class, () -> articleParseService.parseArticles(source));
		verify(strategyFactory).getStrategy(source.getParseMethod());
		verifyNoInteractions(articleParseStrategy);
	}

	@Test
	@DisplayName("파싱_아티클_없음")
	void parseArticles_NoArticles() throws CustomException {
		// Given
		when(strategyFactory.getStrategy(any(ParseMethod.class))).thenReturn(articleParseStrategy);
		when(articleParseStrategy.process(source)).thenReturn(Collections.emptyList());

		// When
		List<ParsedArticle> result = articleParseService.parseArticles(source);

		// Then
		verify(strategyFactory).getStrategy(source.getParseMethod());
		verify(articleParseStrategy).process(source);

		assertTrue(result.isEmpty());
		verifyNoInteractions(articleRepository);
	}


	private ParsedArticle createParsedArticle(String title, String link, Date publishedAt) {
		return ParsedArticle.builder()
			.title(title)
			.link(link)
			.content("Test Content")
			.publishedAt(publishedAt)
			.build();
	}
}