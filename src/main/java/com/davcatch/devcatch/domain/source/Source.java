package com.davcatch.devcatch.domain.source;

import com.davcatch.devcatch.domain.BaseTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 블로그 소스 정보 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Source extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "source_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "main_url", nullable = false, unique = true)
    private String mainUrl;

    @Column(name = "feed_url", unique = true)
    private String feedUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "use_link", nullable = false)
    private boolean useLink;

    @Column(name = "is_foreign", nullable = false)
    private boolean isForeign;

    @Column(name = "parse_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private ParseMethod parseMethod;
}
