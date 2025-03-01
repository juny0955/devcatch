package com.davcatch.devcatch.domain;

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
public class Source extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "source_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "main_url", nullable = false)
    private String mainUrl;

    @Column(name = "feed_url")
    private String feedUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
