package com.davcatch.devcatch.domain;

import com.davcatch.devcatch.service.mail.VerificationInfo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 테이블
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public static Member of(VerificationInfo verificationInfo) {
        return Member.builder()
            .name(verificationInfo.getName())
            .email(verificationInfo.getEmail())
            .build();
    }
}
