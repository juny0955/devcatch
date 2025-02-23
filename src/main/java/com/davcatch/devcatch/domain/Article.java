package com.davcatch.devcatch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.rometools.rome.feed.synd.SyndEntry;

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
    @Lob
    private String link;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "published_at", nullable = false)
    private Date publishedAt;

    public static Article from(Source source, SyndEntry entry, String summary) {
        return Article.builder()
            .source(source)
            .title(entry.getTitle())
            .link(entry.getLink().substring(0, 50))
            .summary(summary)
            .publishedAt(entry.getPublishedDate())
            .build();
    }
}
