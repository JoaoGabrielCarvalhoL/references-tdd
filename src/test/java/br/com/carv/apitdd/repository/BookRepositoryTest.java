package br.com.carv.apitdd.repository;

import br.com.carv.apitdd.model.Book;
import br.com.carv.apitdd.model.BookBuilder;
import br.com.carv.apitdd.service.BookService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookRepositoryTest {


    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Should return true when book exists on database with isbn provided")
    public void should_return_true_when_book_exists() {
        String isbn = "1234567890";
        boolean exists = bookService.existsByIsbn(isbn);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void should_save_book() {

        Book book = BookBuilder.builder()
                .author("Joao Gabriel Carvalho")
                .title("Book Test")
                .isbn("123456789")
                .build();

        bookService.save(book);

    }






}
