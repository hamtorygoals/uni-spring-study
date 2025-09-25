package com.likelion.sbstudy.domain.book.dto.request;

import com.likelion.sbstudy.domain.book.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "UpdateBookRequest DTO", description = "책 부분 수정을 위한 데이터 전송")
public class UpdateBookRequest {

  @Schema(description = "책 제목")
  private String title;

  @Schema(description = "작가")
  private String author;

  @Schema(description = "출판사")
  private String publisher;

  @Schema(description = "가격", example = "20000")
  private Integer price;

  @Schema(description = "책 설명")
  private String description;

  @Schema(description = "출간날짜", example = "2025년 3월 22일")
  private String releaseDate;

  @Schema(description = "카테고리")
  private List<Category> categoryList;
}
