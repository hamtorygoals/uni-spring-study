package com.likelion.sbstudy.domain.user.controller;

import com.likelion.sbstudy.domain.user.dto.request.SignUpRequest;
import com.likelion.sbstudy.domain.user.dto.response.SignUpResponse;
import com.likelion.sbstudy.domain.user.service.UserService;
import com.likelion.sbstudy.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") //항상 api뒤에 복수로 써야함!!
@Tag(name = "User", description = "User 관리 API")
public class UserController {

  private final UserService userService;

  @Operation(summary = "회원가입 API", description = "사용자 회원가입을 위한 API")
  @PostMapping("/sign-up")
  public ResponseEntity<BaseResponse<SignUpResponse>> signUp( //공통응답으로 감싸서, 회원가입 응답으로 보내줌!!
      @RequestBody @Valid SignUpRequest signUpRequest) { //@Valid 값들이 유효하게 잘 왔는지 검사!!
    //System.out.println(signUpRequest.getUsername() + ", " + signUpRequest.getPassword()); //테스트 찍기!
    SignUpResponse signUpResponse = userService.signUp(signUpRequest);
    return ResponseEntity.ok(BaseResponse.success("회원가입에 성공했습니다.", signUpResponse));
  }
}

