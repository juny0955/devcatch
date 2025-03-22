package com.davcatch.devcatch.schedue.article;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.article.Article;
import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.source.SourceRepository;
import com.davcatch.devcatch.schedue.article.dto.ParsedArticle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerService {

	private final SourceRepository sourceRepository;
	private final ArticleSchedulerTask articleSchedulerTask;

	public void createNewArticle() {
		List<Source> sources = sourceRepository.findAllByIsActiveTrue();

		List<CompletableFuture<Void>> futures = sources.stream()
			.map(source -> CompletableFuture.runAsync(() -> {
				try {
					articleSchedulerTask.processSource(source);
				} catch (Exception e) {
					log.error("[{}] 소스 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
				}
			}))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
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
