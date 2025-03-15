package com.davcatch.devcatch.service.schedue.article;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.article.ArticleTag;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.exception.CustomException;
import com.davcatch.devcatch.exception.ErrorCode;
import com.davcatch.devcatch.integration.gpt.GptSummaryService;
import com.davcatch.devcatch.integration.gpt.response.GptResponse;
import com.davcatch.devcatch.repository.SourceRepository;
import com.davcatch.devcatch.repository.TagRepository;
import com.davcatch.devcatch.service.article.ArticleService;
import com.davcatch.devcatch.service.schedue.article.dto.Content;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;
import com.davcatch.devcatch.service.schedue.article.strategy.ArticleParseStrategy;
import com.davcatch.devcatch.service.schedue.article.strategy.ArticleParseStrategyFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerService {

	private final SourceRepository sourceRepository;
	private final ArticleService articleService;
	private final ArticleParseStrategyFactory articleParseStrategyFactory;
	private final GptSummaryService gptSummaryService;
	private final TagRepository tagRepository;
	private final ContentParser contentParser;

	public void createNewArticle() {
		List<Source> sources = sourceRepository.findAllByIsActiveTrue();

		for (Source source : sources) {
			log.info("[{}] Article 생성 스케줄러 시작", source.getName());

			try {
				ArticleParseStrategy articleParseStrategy = articleParseStrategyFactory.getStrategy(source.getParseMethod());
				List<ParsedArticle> parsedArticles = articleParseStrategy.process(source);

				if (parsedArticles.isEmpty()) {
					log.info("[{}] 새로운 Article이 없습니다.", source.getName());
					continue;
				}

				int count = 0;
				for (ParsedArticle parsedArticle : parsedArticles) {
					if (!articleFilter(parsedArticle, source))
						continue;

					GptResponse response = gptSummaryService.getSummary(parsedArticle.getContent());
					Content content = contentParser.parseContent(response);

					Article article = Article.of(source, parsedArticle, content.getSummary());
					for (TagType tagType : content.getTag()) {
						Tag tag = tagRepository.findByTagType(tagType).orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));

						article.addArticleTag(ArticleTag.of(article, tag));
					}

					articleService.save(article);
					count++;
				}

				log.info("[{}] Article 생성 스케줄러 종료, NEW : {}개", source.getName(), count);
			} catch (CustomException e) {
				log.info("[{}] Article 생성 스케줄러 실행중 오류 발생 : {}", source.getName(), e.getErrorCode());
			}
		}
	}

	/** Article 필터
	 * 해당 Link가 중복인지 확인
	 * 해당 Source에 해당하는 데이터가 없을 시 어제 날짜 기준 이후 데이터인지 확인
	 * 해당 Source에 해당하는 데이터가 있을 시 마지막 게시글 이후 데이터인지 확인
	 *
	 * @param parsedArticle 새로운 아티클
	 * @param source 해당 Source
	 */
	private boolean articleFilter(ParsedArticle parsedArticle, Source source) {
		if (articleService.checkExistsLink(parsedArticle.getLink()))
			return false;

		Optional<Article> lastPublishedArticle = articleService.getByLastPublishedArticle(source.getId());
		Date date;

		if (lastPublishedArticle.isEmpty()) {
			LocalDate yesterDay = LocalDate.now().minusDays(1);
			date = Date.from(yesterDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
		} else
			date = lastPublishedArticle.get().getPublishedAt();

		return parsedArticle.getPublishedAt().after(date) || parsedArticle.getPublishedAt().equals(date);
	}
}
