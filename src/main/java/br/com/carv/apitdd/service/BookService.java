package br.com.carv.apitdd.service;

import br.com.carv.apitdd.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    Boolean existsByIsbn(String isbn);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);

    Optional<Book> getBookByIsbn(String s);
}
