package com.mycompany.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // @EntityListeners 사용을 위한 Annotation
@SpringBootApplication
public class Be222ndTeam1ProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(Be222ndTeam1ProjectApplication.class, args);
  }

}
