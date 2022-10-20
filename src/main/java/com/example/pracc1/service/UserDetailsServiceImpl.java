package com.example.pracc1.service;

import com.example.pracc1.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final com.example.pracc1.repository.MemberRepository memberRepository;

    @Override//재정의 찾아보기
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //optional<member> -> optional<userDetails> ->userdetails
        return memberRepository.findByNickname(username)
                .map(this::createUserDetails)
                .orElseThrow(()->new UsernameNotFoundException("입력하신 회원 닉네임 정보가 없습니다."));

    }


    //우리가 만든 멤버를 가지고 유저디테일즈로 변환을 시켜준다!! -> 멤버 리폳지토리에서 유저네임에서
    private UserDetails createUserDetails(Member member){
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().toString());

        return new User(String.valueOf(member.getId()),
                member.getPassword(),
                Collections.singleton(grantedAuthority));
    }
}
