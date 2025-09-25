package com.likelion.sbstudy.domain.book.exception;

import com.likelion.sbstudy.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookErrorCode implements BaseErrorCode {

  BOOK_ALREADY_EXISTS("BOOK4001", "이미 존재하는 책입니다.", HttpStatus.BAD_REQUEST),
  BOOK_NOT_FOUND("BOOK4040", "존재하지 않는 책입니다.", HttpStatus.NOT_FOUND);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
