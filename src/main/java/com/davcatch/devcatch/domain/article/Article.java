package com.davcatch.devcatch.domain.article;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.davcatch.devcatch.domain.source.Source;
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

    @Column(name = "link", nullable = false, unique = true)
    private String link;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent;

    @Column(name = "published_at", nullable = false)
    private Date publishedAt;

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ArticleTag> articleTags;

    public static Article of(Source source, ParsedArticle parsedArticle, String summary) {
        return Article.builder()
            .source(source)
            .title(parsedArticle.getTitle())
            .link(parsedArticle.getLink())
            .summary(summary)
            .publishedAt(parsedArticle.getPublishedAt())
            .articleTags(new ArrayList<>())
            .build();
    }

    /**
     * Article 발송
     * isSent True로 변경
     */
    public void sendArticle() {
        this.isSent = true;
    }

    /**
     * 아티클 태그 추가
     * @param articleTag 아티클 태그
     */
    public void addArticleTag(ArticleTag articleTag) {
        this.articleTags.add(articleTag);
    }
}
