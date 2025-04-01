package com.davcatch.devcatch.common.scheduler.article;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.web.service.source.SourceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerService {

	private final SourceService sourceService;
	private final ArticleSchedulerTask articleSchedulerTask;
	private final Executor schedulerTaskExecutor;

	public void createNewArticle() {
		List<Source> sources = sourceService.getActiveSources();
		log.info("총 {}개 소스 처리 시작", sources.size());

		int batchSize = 5;

		for (int i=0; i<sources.size(); i+=batchSize) {
			int endIndex = Math.min(i + batchSize, sources.size());
			List<Source> batchSources = sources.subList(i, endIndex);

			List<CompletableFuture<Void>> futures = batchSources.stream()
				.map(source -> CompletableFuture.runAsync(() -> {
						articleSchedulerTask.processSource(source);
				}, schedulerTaskExecutor))
				.toList();

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		}
	}
}
