package com.likelion.sbstudy.domain.user.service;

import com.likelion.sbstudy.domain.user.dto.request.SignUpRequest;
import com.likelion.sbstudy.domain.user.dto.response.SignUpResponse;
import com.likelion.sbstudy.domain.user.entity.User;
import com.likelion.sbstudy.domain.user.exception.UserErrorCode;
import com.likelion.sbstudy.domain.user.mapper.UserMapper;
import com.likelion.sbstudy.domain.user.repository.UserRepository;
import com.likelion.sbstudy.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//서비스 작성할 땐, 이렇게 세 어노테이션 항상 넣기!!
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public SignUpResponse signUp(SignUpRequest request) {
    System.out.println(request.getUsername() + ", " + request.getPassword());
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new CustomException(UserErrorCode.USERNAME_ALREADY_EXISTS);
    }


    // 비밀번호 인코딩
    String encodedPassword = passwordEncoder.encode(request.getPassword()); //비밀번호를 인코딩, 즉 암호화해주기!!

    // 유저 엔티티 생성
    User user = User.builder()
        .username(request.getUsername())
        .password(encodedPassword)
        .build();

    // 저장 및 로깅
    User savedUser = userRepository.save(user);
    log.info("새로운 사용자 생성: {}", savedUser.getUsername());

    return userMapper.toSignUpResponse(savedUser);
  }
}

