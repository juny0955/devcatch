package com.davcatch.devcatch.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class Member extends BaseTime implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "subscribe_all", nullable = false)
    private boolean subscribeAll;

    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MemberTag> memberTags = new ArrayList<>();

    public static Member from(VerificationInfo verificationInfo, String encodedPassword) {
        return Member.builder()
            .name(verificationInfo.getName())
            .email(verificationInfo.getEmail())
            .password(encodedPassword)
            .subscribeAll(true)
            .memberTags(new ArrayList<>())
            .build();
    }

    public void changeSubscription(boolean subscribeAll) {
        this.subscribeAll = subscribeAll;
    }

    public void clearMemberTags() {
        this.memberTags.clear();
    }

    public void addMemberTags(List<MemberTag> memberTags) {
        this.memberTags.addAll(memberTags);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
