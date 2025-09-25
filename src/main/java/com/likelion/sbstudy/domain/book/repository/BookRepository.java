package com.likelion.sbstudy.domain.book.repository;

import com.likelion.sbstudy.domain.book.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

  Optional<Book> findByTitleAndAuthor(String title, String author);

  @EntityGraph(attributePaths = {"bookImages"})
  List<Book> findAll(); // 전체 조회 시 이미지까지 함께 로딩

  @EntityGraph(attributePaths = {"bookImages"})
  Optional<Book> findById(Long id); // 단건 조회 시 이미지까지 함께 로딩

}
