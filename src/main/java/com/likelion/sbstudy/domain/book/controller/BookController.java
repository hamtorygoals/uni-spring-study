package com.likelion.sbstudy.domain.book.controller;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.request.UpdateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.service.BookService;
import com.likelion.sbstudy.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
@Tag(name = "Book", description = "책 관련 API")
public class BookController {

  private final BookService bookService;

  @Operation(
      summary = "새 책 등록",
      description = "새로운 책을 등록하고, 등록된 책 정보를 반환합니다. (201 Created)")
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<BookResponse>> createBook(
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart(value = "book") @Valid CreateBookRequest request,
      @Parameter(description = "책 이미지들",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
      @RequestPart(value = "images")
      List<MultipartFile> images) {
    BookResponse response = bookService.createBook(request, images);
    return ResponseEntity.ok(BaseResponse.success("책 생성에 성공하였습니다.", response));
  }

  @Operation(summary = "책 전체 조회", description = "등록된 모든 책 목록을 반환합니다.")
  @GetMapping("")
  public ResponseEntity<BaseResponse<List<BookResponse>>> getAllBooks() {
    List<BookResponse> responses = bookService.getAllBooks();
    return ResponseEntity.ok(BaseResponse.success("책 전체 조회에 성공하였습니다.", responses));
  }

  @Operation(summary = "책 단건 조회", description = "책 ID로 단일 책 정보를 반환합니다.")
  @GetMapping("/{bookId}")
  public ResponseEntity<BaseResponse<BookResponse>> getBook(@PathVariable Long bookId) {
    BookResponse response = bookService.getBook(bookId);
    return ResponseEntity.ok(BaseResponse.success("책 단건 조회에 성공하였습니다.", response));
  }

  @Operation(
      summary = "책 수정",
      description = "책 정보를 부분 수정합니다. 새 이미지가 올라오면 기존 이미지는 모두 삭제 후 교체합니다.")
  @PatchMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse<BookResponse>> updateBook(
      @PathVariable Long bookId,
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart(value = "book") @Valid UpdateBookRequest request,
      @Parameter(description = "교체할 새 이미지들(옵션). 첨부되면 기존 이미지는 모두 삭제됩니다.",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
      @RequestPart(value = "images", required = false) List<MultipartFile> newImages
  ) {
    BookResponse response = bookService.updateBook(bookId, request, newImages);
    return ResponseEntity.ok(BaseResponse.success("책 수정에 성공하였습니다.", response));
  }

  @Operation(summary = "책 삭제", description = "책 및 연결 이미지 삭제")
  @DeleteMapping("/{bookId}")
  public ResponseEntity<BaseResponse<Void>> deleteBook(@PathVariable Long bookId) {
    bookService.deleteBook(bookId);
    return ResponseEntity.ok(BaseResponse.success("책 삭제에 성공하였습니다.", null));
  }
}
