package com.likelion.sbstudy.domain.book.service;

import com.likelion.sbstudy.domain.book.dto.request.CreateBookRequest;
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
}
