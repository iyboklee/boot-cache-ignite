package com.github.iyboklee.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.github.iyboklee.model.Book;

@Repository
public class SlowBookRepository implements BookRepository {

    @Override
    @Cacheable(cacheNames = "books")
    public Book findByIsbn(String isbn) {
        simulateSlowService();

        return new Book(isbn, "Some book");
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(1_000L);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
