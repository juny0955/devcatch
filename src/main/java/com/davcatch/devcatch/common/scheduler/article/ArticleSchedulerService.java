package com.davcatch.devcatch.common.scheduler.article;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.stereotype.Service;

import com.davcatch.devcatch.domain.source.Source;
import com.davcatch.devcatch.domain.tag.Tag;
import com.davcatch.devcatch.domain.tag.TagType;
import com.davcatch.devcatch.web.service.source.SourceService;
import com.davcatch.devcatch.web.service.tag.TagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleSchedulerService {

	private static final int ARTICLE_BATCH_SIZE = 5;

	private final SourceService sourceService;
	private final TagService tagService;
	private final ArticleSchedulerTask articleSchedulerTask;
	private final Executor schedulerTaskExecutor;

	public void createNewArticle() {
		List<Source> sources = sourceService.getActiveSources();
		Map<TagType, Tag> tagMap = tagService.getAllTagsConvertMap();
		log.info("총 {}개 소스 처리 시작", sources.size());

		for (int i = 0; i < sources.size(); i += ARTICLE_BATCH_SIZE) {
			int endIndex = Math.min(i + ARTICLE_BATCH_SIZE, sources.size());
			List<Source> batchSources = sources.subList(i, endIndex);

			List<CompletableFuture<Void>> futures = batchSources.stream()
				.map(source -> CompletableFuture.runAsync(() -> {
					articleSchedulerTask.processSource(source, tagMap);
				}, schedulerTaskExecutor))
				.toList();

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		}

	}
}
