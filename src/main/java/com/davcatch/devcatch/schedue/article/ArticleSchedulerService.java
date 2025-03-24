package com.davcatch.devcatch.schedue.article;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.source.SourceRepository;
import com.davcatch.devcatch.service.source.SourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerService {

	private final SourceService sourceService;
	private final ArticleSchedulerTask articleSchedulerTask;

	public void createNewArticle() {
		List<Source> sources = sourceService.getActiveSources();

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
}
