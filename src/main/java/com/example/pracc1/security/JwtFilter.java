package com.example.pracc1.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { //로그인이 필요한 요청에시행됨

    private final TokenProvider tokenProvider;

    // 실제 필터링 로직
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext에 저장
    @Override//토큰의 정보가 일치하는지 불일치하는지 확인하는부분
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if(StringUtils.hasText(token) && tokenProvider.validateToken(token)){ //유효한지확인

            //토큰을 통해 authentication을 생성하고 가져온다.
            Authentication authentication = tokenProvider.getAuthentication(token); // 시큐리티에 저장함!!
            //SecurityContext 를 가져온다.
            SecurityContextHolder.getContext().setAuthentication(authentication); //시큐리티 컨텍스트에 저장
        }

        filterChain.doFilter(request, response);

    }

    // 헤더에서 토큰을 가져온다.(클라이언트에서 토큰을 헤더에 담아 보내주기 때문에)
    private String resolveToken(HttpServletRequest request){

        String token = request.getHeader(TokenProvider.AUTHORIZATION_HEADER);

        //토큰 앞에 걸 제거해서 토큰만 남김
        if(StringUtils.hasText(token) && token.startsWith(TokenProvider.TOKEN_PREFIX)){
            return token.substring(7);
        }

        return null;
    }
}
