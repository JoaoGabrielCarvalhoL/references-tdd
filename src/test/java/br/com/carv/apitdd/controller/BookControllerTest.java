package br.com.carv.apitdd.controller;

import br.com.carv.apitdd.exception.BusinessException;
import br.com.carv.apitdd.model.Book;
import br.com.carv.apitdd.model.BookBuilder;
import br.com.carv.apitdd.model.dto.BookDTO;
import br.com.carv.apitdd.model.dto.BookDTOBuilder;
import br.com.carv.apitdd.repository.BookRepository;
import br.com.carv.apitdd.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Arrays;
import java.util.Optional;

    @ExtendWith(SpringExtension.class)
    @ActiveProfiles("test")
    @WebMvcTest(controllers = {BookController.class})
    @AutoConfigureMockMvc
public class BookControllerTest {
    static String BOOK_API = "/books";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @Test
    @DisplayName("must successfully create a book")
    public void createBookTest() throws Exception {

        BookDTO book = BookDTOBuilder.builder()
                .author("Joao Gabriel Carvalho")
                .title("Book Test")
                .isbn("123456789")
                .build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willReturn(BookBuilder.builder().id(1L).author("Joao Gabriel Carvalho")
                        .title("Book Test")
                        .isbn("123456789")
                        .build());

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
    }
    @Test
    @DisplayName("should throw validation error when there is not enough data to create the book.")
    public void createInvalidBookTest() throws Exception {

        BookDTO book = BookDTOBuilder.builder()
                .author("")
                .title("")
                .isbn("")
                .build();

        String json = new ObjectMapper().writeValueAsString(book);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Fields not be empty"));

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("should throw validation error when isbn not unique")
    public void create_book_with_duplicated_isbn() throws Exception {

        BookDTO book = BookDTOBuilder.builder()
                .author("Joao Gabriel Carvalho")
                .title("Book Test")
                .isbn("123456789")
                .build();

        String json = new ObjectMapper().writeValueAsString(book);

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("ISBN is not valid!"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("ISBN is not valid!"));

    }

    @Test
    @DisplayName("should throw validation when isbn is duplicated")
    public void should_not_save_book_with_duplicated_isbn() {

        Book book = BookBuilder.builder()
                .author("Joao Gabriel Carvalho")
                .title("Book Test")
                .isbn("123456789")
                .build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("ISBN is not valid!"));


        Mockito.verify(bookRepository, Mockito.never()).save(book);

    }

    @Test
    public void getBookDetailsTest() throws Exception {

        Long id = 1L;
        Book book = BookBuilder.builder()
                .id(id)
                .author("Joao Gabriel Carvalho")
                .title("Book Test")
                .isbn("123456789")
                .build();

        BDDMockito.given(bookService.getById(book.getId())).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("should throw exception when book not found on database")
    public void book_not_found_test() throws Exception {

        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/"+1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void delete_book_test() throws Exception {

        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(BookBuilder.builder().id(11L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void delete_book_invalid() throws Exception {

        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void update_book_test() throws Exception {

        Book book = BookBuilder.builder()
                .id(1L)
                .author("Joao Gabriel Carvalho")
                .title("Book Test")
                .isbn("123456789")
                .build();

        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(book);

        Book bookUpdated = BookBuilder.builder().id(1L).author("updated author")
                .title("Book Test").isbn("123456789").build();

        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(bookUpdated));

        BDDMockito.given(bookService.update(bookUpdated)).willReturn(bookUpdated);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    public void update_book_invalid() throws Exception {

        Book book = BookBuilder.builder()
                .build();

        String json = new ObjectMapper().writeValueAsString(book);

        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void find_book_test() throws Exception {
        Long id = 1L;

        Book book = BookBuilder.builder().id(id).title("test").author("test").isbn("123").build();
        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryString)).accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));


    }
}
