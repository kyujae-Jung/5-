package com.example.pracc1.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Member extends Timestamped{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String nickname;

    @Column
    private String password;

    @Column
    @Enumerated(value = EnumType.STRING)
    private com.example.pracc1.entity.MemberRole role;

    public Member(String nickname, String password){
        this.nickname = nickname;
        this.password = password;
        this.role = com.example.pracc1.entity.MemberRole.MEMBER;
    }
}
