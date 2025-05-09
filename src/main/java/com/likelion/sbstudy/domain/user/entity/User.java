package com.likelion.sbstudy.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.likelion.sbstudy.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) //NoArgs엔 반드시 Pretected로 해야함!
@AllArgsConstructor
@Table(name = "users") //import jakarta로!
public class User extends BaseTimeEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "username", nullable = false, unique = true) //유저네임은 사용자의 아이디!! unique = true 해줘서, 아이디가 겹치면 안돼!!
  //근데 만약 예외처리를 안했으면, 에러남!!
  private String username;

  @JsonIgnore  //제이슨 형식으로 데이터 응답할 때, 패스워드 필드에 대해선, 절대 보내지 않겠다!!
  @Column(name = "password", nullable = false) //비밀번호는 보호해야하니까
  private String password;

  @JsonIgnore  //보내지 않을 것이란 걸 명시
  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "role", nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default //빌더될 때, 필드를 명시해주지 않아도 기본값으로 USER로 들어간다.
  private Role role = Role.USER;  //이넘 타입을 디폴트로 유저로 둔다.

  public void createRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}