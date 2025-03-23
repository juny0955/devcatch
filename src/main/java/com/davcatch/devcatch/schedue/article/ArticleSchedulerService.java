package com.davcatch.devcatch.schedue.article;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.repository.source.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerService {

	private final SourceRepository sourceRepository;
	private final ArticleSchedulerTask articleSchedulerTask;
	private final Executor schedulerTaskExecutor;

	public void createNewArticle() {
		List<Source> sources = sourceRepository.findAllByIsActiveTrue();
		log.info("총 {}개 소스 처리 시작", sources.size());

		List<CompletableFuture<Void>> futures = sources.stream()
			.map(source -> CompletableFuture.runAsync(() -> {
				try {
					articleSchedulerTask.processSource(source);
				} catch (Exception e) {
					log.error("[{}] 소스 처리 중 오류 발생: {}", source.getName(), e.getMessage(), e);
				}
			}, schedulerTaskExecutor))
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}
}
