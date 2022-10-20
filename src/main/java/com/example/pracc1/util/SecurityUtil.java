package com.example.pracc1.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class SecurityUtil {

    public static Long getCurrentMemberId(){

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null){
            throw new RuntimeException("인증 정보가 존재하지 않습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
