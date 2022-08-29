package br.com.carv.apitdd.repository;

import br.com.carv.apitdd.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByIsbn(String isbn);

    Boolean existsByIsbn(String isbn);
}
