package com.github.iyboklee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.github.iyboklee.repository.BookRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired private ConfigurableApplicationContext context;

    @Autowired private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info(".... Fetching books from slow repository");
        fetchBooks();

        log.info(".... Fetching books from ignite cache");
        fetchBooks();

        System.exit(SpringApplication.exit(context));
    }

    private void fetchBooks() {
        log.info("isbn-1234 --> {}", bookRepository.findByIsbn("isbn-1234"));
        log.info("isbn-1235 --> {}", bookRepository.findByIsbn("isbn-1235"));
        log.info("isbn-1236 --> {}", bookRepository.findByIsbn("isbn-1236"));
        log.info("isbn-1237 --> {}", bookRepository.findByIsbn("isbn-1237"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
