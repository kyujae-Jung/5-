package com.example.pracc1.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    MEMBER(Authority.MEMBER);

    private final String authority;

    MemberRole(String authority){
        this.authority = authority;
    }

    public static class Authority{
        private static final String MEMBER = "ROLE_MEMBER";
    }
}
