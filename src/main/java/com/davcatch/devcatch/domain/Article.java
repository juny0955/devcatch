package com.davcatch.devcatch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.davcatch.devcatch.integration.gpt.response.GptResponse;
import com.davcatch.devcatch.service.schedue.article.dto.ParsedArticle;

/**
 * 아티클 정보 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent = false;

    @Column(name = "published_at", nullable = false)
    private Date publishedAt;

    public static Article of(Source source, ParsedArticle parsedArticle, GptResponse response) {
        return Article.builder()
            .source(source)
            .title(parsedArticle.getTitle())
            .link(parsedArticle.getLink())
            .summary(response.getChoices().get(0).getMessage().getContent())
            .publishedAt(parsedArticle.getPublishedAt())
            .build();
    }

    /**
     * Article 발송
     * isSent True로 변경
     */
    public void sendArticle() {
        this.isSent = true;
    }
}
