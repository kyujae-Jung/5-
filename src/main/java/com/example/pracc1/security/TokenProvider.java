package com.example.pracc1.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component  //찾아보기 ioc컨테이너에 bean으로 등록해달라는뜻 찾아보기
@Slf4j   //로깅이 뭔지 찾아보기 sout 랑 다른첨 찾기
public class TokenProvider { //로그인 할때

    //jwt를 전달할 때 헤더의 key값을 정의
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // jwt를 전달할 때 token의 앞에 붙혀줄 문자열(일종의 규칙)
    public static final String TOKEN_PREFIX = "Bearer ";
    // jwt 내부의 claim에 key값으로 정의할 문자열
    public static final String AUTHORITY_KEY = "auth";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30분


    private final Key key; //jwt에 검증을 하기위해 사용하는 키값 //final이라서 초기값이 무조건 필요

    public TokenProvider(@Value("${jwt.secret}") String secretKey){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    //UsernamePasswordAuthenticationToken - authentication
    //spring security 내부에서 사용하는 인증 객체인 authentication 객체를 받아 토큰을 생성
    public com.example.pracc1.dto.TokenDto generateToken(Authentication authentication){//계정정보를 받아 토큰을 생성
        //ROLE_USER,ROLE_ADMIN
        //ROLE_USER.ROLE_ADMIN
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        Date accessTokenExpiresIn = new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        //Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // 반드시 고유한 값을 넣어줘야 한다.
                .claim(AUTHORITY_KEY, authorities) //"auth" : "ROLE_MEMBER" 권한정보를 저장   /jwt에서 사용하는 공간
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new com.example.pracc1.dto.TokenDto(accessToken);
    }

    //jwt토큰을 받아 spring security에서 사용하는 인증 객체인 authentication 객체를 반환
    public Authentication getAuthentication(String accessToken){ //로그인 해야만 사용할 수 있는 기능을 쓸때
        //복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITY_KEY)==null) throw new RuntimeException("권한 정보가 없는 토큰입니다.");

        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITY_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserDetails principal = new User(claims.getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e){
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e){
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e){
            log.error("지원되지 않는 JWT토큰입니다.");
        } catch (IllegalArgumentException e){
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
