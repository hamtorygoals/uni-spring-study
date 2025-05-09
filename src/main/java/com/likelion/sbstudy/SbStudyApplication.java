package com.likelion.sbstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SbStudyApplication {

  public static void main(String[] args) {
    SpringApplication.run(SbStudyApplication.class, args);
  }

}
