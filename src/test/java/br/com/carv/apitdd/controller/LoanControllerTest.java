package br.com.carv.apitdd.controller;

import br.com.carv.apitdd.exception.BusinessException;
import br.com.carv.apitdd.model.BookBuilder;
import br.com.carv.apitdd.model.Loan;
import br.com.carv.apitdd.model.LoanBuilder;
import br.com.carv.apitdd.model.dto.LoanDTO;
import br.com.carv.apitdd.model.dto.LoanDTOBuilder;
import br.com.carv.apitdd.model.dto.ReturnedLoadDTO;
import br.com.carv.apitdd.model.dto.ReturnedLoadDTOBuilder;
import br.com.carv.apitdd.service.BookService;
import br.com.carv.apitdd.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static String LOAN_API = "/api/loans";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    public void create_loan_test() throws Exception {

        LoanDTO loanDto = LoanDTOBuilder.builder().isbn("123").customer("Joao").build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(BookBuilder.builder().id(1L).isbn("123").build()));

        Loan loan = LoanBuilder.builder().id(1L).customer("Joao")
                .book(BookBuilder.builder().id(1L).isbn("123").build()).loanDate(LocalDate.now())
                .build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    @Test
    public void invalid_create_loan() throws Exception {
        LoanDTO loanDto = LoanDTOBuilder.builder().isbn("123").customer("Joao").build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    public void loaned_book_error_on_create_loan() throws Exception {
        LoanDTO loanDto = LoanDTOBuilder.builder().isbn("123").customer("Joao").build();
        String json = new ObjectMapper().writeValueAsString(loanDto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(BookBuilder.builder().id(1L).isbn("123").build()));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    public void return_book_test() throws Exception {

        ReturnedLoadDTO dto = ReturnedLoadDTOBuilder.builder().returned(true).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(LoanBuilder.builder().id(1L).build()));

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(loanService,Mockito.times(1)).update(LoanBuilder.builder().id(1L).build());
    }

    @Test
    public void return_invalid_book_test() throws Exception {

        ReturnedLoadDTO dto = ReturnedLoadDTOBuilder.builder().returned(true).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}


