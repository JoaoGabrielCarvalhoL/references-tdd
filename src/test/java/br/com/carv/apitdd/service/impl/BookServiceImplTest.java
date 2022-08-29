package br.com.carv.apitdd.service.impl;

import br.com.carv.apitdd.exception.BusinessException;
import br.com.carv.apitdd.model.Book;
import br.com.carv.apitdd.model.BookBuilder;
import br.com.carv.apitdd.repository.BookRepository;
import br.com.carv.apitdd.service.BookService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceImplTest {

    private BookService bookService;
    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("should save a book")
    public void should_save_book() {
        Book book = BookBuilder.builder().title("Book Test")
                .author("Author Test")
                .isbn("123456789-00").build();

        Mockito.when(bookRepository.save(book)).thenReturn(BookBuilder.builder()
                .id(1L).title("Book Test").author("Author Test")
                .isbn("123456789-00").build());

        Book savedBook = bookService.save(book);
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    public void should_return_book_by_id() {
        Long id = 1L;

        Book book = new Book();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById(id);
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
    }

    @Test
    public void book_not_found_test() {
        Long id = 1L;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = bookService.getById(id);
        Assertions.assertThat(book.isPresent()).isFalse();

    }

    @Test
    public void should_delete_book() {
        Book book = BookBuilder.builder().id(1L).build();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    public void delete_invalid_book() {
        Book book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));
        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    public void should_update_book() {
        Long id = 1L;
        Book book = BookBuilder.builder().id(id).build();
        Book updatedBook = BookBuilder.builder().id(id).author("test update").title("test update").isbn("123").build();

       Mockito.when(bookRepository.save(book)).thenReturn(updatedBook);

       book = bookService.update(updatedBook);

       Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
       Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
       Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
       Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    public void update_invalid_book() {
        Book book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    public void find_book_test() {
        Book book = BookBuilder.builder().title("test").author("test").isbn("123456789").build();

        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100), 1);
        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> pageResult = bookService.find(book, PageRequest.of(0,100));

        Assertions.assertThat(pageResult.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(pageResult.getContent()).isEqualTo(Arrays.asList(book));
        Assertions.assertThat(pageResult.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(pageResult.getPageable().getPageSize()).isEqualTo(100);

    }

    @Test
    public void should_return_book_by_isbn() {
        String isbn = "123";
        Mockito.when(bookRepository.findBookByIsbn(isbn))
                .thenReturn(Optional.of(BookBuilder.builder().id(1L).build()));

        Optional<Book> bookByIsbn = bookService.getBookByIsbn(isbn);

        Assertions.assertThat(bookByIsbn.isPresent()).isTrue();
        Assertions.assertThat(bookByIsbn.get().getId()).isEqualTo(1L);
        Assertions.assertThat(bookByIsbn.get().getIsbn()).isEqualTo(isbn);

        Mockito.verify(bookRepository, Mockito.times(1)).findBookByIsbn(isbn);
    }

}
