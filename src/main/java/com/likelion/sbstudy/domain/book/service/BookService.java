package com.likelion.sbstudy.domain.book.service;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
import com.likelion.sbstudy.domain.book.dto.request.UpdateBookRequest;
import com.likelion.sbstudy.domain.book.dto.response.BookResponse;
import com.likelion.sbstudy.domain.book.entity.Book;
import com.likelion.sbstudy.domain.book.entity.BookImage;
import com.likelion.sbstudy.domain.book.exception.BookErrorCode;
import com.likelion.sbstudy.domain.book.mapper.BookMapper;
import com.likelion.sbstudy.domain.book.repository.BookRepository;
import com.likelion.sbstudy.global.exception.CustomException;
import com.likelion.sbstudy.global.s3.entity.PathName;
import com.likelion.sbstudy.global.s3.service.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

  private final BookRepository bookRepository;
  private final S3Service s3Service;
  private final BookMapper bookMapper;

  @Transactional
  public BookResponse createBook(CreateBookRequest request, List<MultipartFile> images) {

    if (bookRepository.findByTitleAndAuthor(request.getTitle(), request.getAuthor()).isPresent()) {
      throw new CustomException(BookErrorCode.BOOK_ALREADY_EXISTS);
    }

    Book book = Book.builder()
        .title(request.getTitle())
        .author(request.getAuthor())
        .publisher(request.getPublisher())
        .price(request.getPrice())
        .description(request.getDescription())
        .categoryList(request.getCategoryList())
        .releaseDate(request.getReleaseDate())
        .build();

    List<BookImage> bookImages = images.stream()
        .filter(image -> !image.isEmpty())
        .map(image -> {
          String imageUrl = s3Service.uploadFile(PathName.FOLDER1, image);
          return BookImage.builder()
              .imageUrl(imageUrl)
              .book(book)
              .build();
        })
        .toList();

    book.addBookImages(bookImages);

    bookRepository.save(book);

    return bookMapper.toBookResponse(book);
  }

  @Transactional(readOnly = true)
  public List<BookResponse> getAllBooks() {
    List<Book> books = bookRepository.findAll();
    return books.stream().map(bookMapper::toBookResponse).toList();
  }

  @Transactional(readOnly = true)
  public BookResponse getBook(Long bookId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));
    return bookMapper.toBookResponse(book);
  }

  @Transactional
  public BookResponse updateBook(
      Long bookId,
      UpdateBookRequest request,
      List<MultipartFile> newImages // null 또는 비어있으면 이미지 유지
  ) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));

    // 1) 메타데이터 부분 수정 (NULL 무시)
    if (request.getTitle() != null) {
      set(book, "title", request.getTitle());
    }
    if (request.getAuthor() != null) {
      set(book, "author", request.getAuthor());
    }
    if (request.getPublisher() != null) {
      set(book, "publisher", request.getPublisher());
    }
    if (request.getPrice() != null) {
      set(book, "price", request.getPrice());
    }
    if (request.getDescription() != null) {
      set(book, "description", request.getDescription());
    }
    if (request.getReleaseDate() != null) {
      set(book, "releaseDate", request.getReleaseDate());
    }
    if (request.getCategoryList() != null && !request.getCategoryList().isEmpty()) {
      set(book, "categoryList", request.getCategoryList());
    }

    // 2) 새 이미지가 첨부되었으면 → 기존 이미지 전부 삭제 후 교체
    boolean hasNewImages =
        newImages != null && newImages.stream().anyMatch(f -> f != null && !f.isEmpty());
    if (hasNewImages) {
      // (1) 기존 이미지 S3에서 삭제
      for (BookImage img : book.getBookImages()) {
        try {
          if (img.getImageUrl() != null && !img.getImageUrl().isBlank()) {
            s3Service.deleteFileByUrl(img.getImageUrl());
          }
        } catch (Exception e) {
          log.warn("S3 이미지 삭제 실패(무시): {}", img.getImageUrl(), e);
        }
      }
      // (2) 연관관계 정리 (orphanRemoval=true 이므로 DB에서 삭제됨)
      book.getBookImages().clear();

      // (3) 새 이미지 업로드 & 연결
      List<BookImage> replaced = newImages.stream()
          .filter(f -> f != null && !f.isEmpty())
          .map(f -> {
            String url = s3Service.uploadFile(PathName.FOLDER1, f);
            return BookImage.builder().imageUrl(url).book(book).build();
          })
          .toList();
      book.getBookImages().addAll(replaced);
    }

    return bookMapper.toBookResponse(book);
  }

  @Transactional
  public void deleteBook(Long bookId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new CustomException(BookErrorCode.BOOK_NOT_FOUND));

    // 연결된 이미지 S3에서 삭제 (URL 기반)
    for (BookImage img : book.getBookImages()) {
      try {
        if (img.getImageUrl() != null && !img.getImageUrl().isBlank()) {
          s3Service.deleteFileByUrl(img.getImageUrl()); // << URL 삭제
        }
      } catch (Exception e) {
        log.warn("S3 이미지 삭제 실패(무시): {}", img.getImageUrl(), e);
      }
    }

    bookRepository.delete(book);
  }

  // ======= 엔티티에 setter가 없으므로 내부 편의 set 사용 =======
  private void set(Object target, String field, Object value) {
    try {
      var f = target.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new IllegalStateException("필드 업데이트 실패: " + field, e);
    }
  }

}
