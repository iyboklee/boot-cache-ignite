package com.github.iyboklee.repository;

import com.github.iyboklee.model.Book;

public interface BookRepository {

    Book findByIsbn(String isbn);

}
