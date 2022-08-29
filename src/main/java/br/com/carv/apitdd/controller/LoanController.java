package br.com.carv.apitdd.controller;

import br.com.carv.apitdd.model.Book;
import br.com.carv.apitdd.model.Loan;
import br.com.carv.apitdd.model.LoanBuilder;
import br.com.carv.apitdd.model.dto.BookDTO;
import br.com.carv.apitdd.model.dto.LoanDTO;
import br.com.carv.apitdd.model.dto.LoanFilterDTO;
import br.com.carv.apitdd.model.dto.ReturnedLoadDTO;
import br.com.carv.apitdd.service.BookService;
import br.com.carv.apitdd.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    private final BookService bookService;

    private ModelMapper mapper;

    public LoanController(LoanService loanService, BookService bookService, ModelMapper mapper) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanDTO loanDTO) {
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = LoanBuilder.builder().customer(loanDTO.getCustomer())
                .loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);
        return entity.getId();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable("id") Long id, @RequestBody ReturnedLoadDTO dto) {
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> findLoan(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> pageLoanDto = result.getContent()
                .stream().map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = mapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = mapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(pageLoanDto, pageRequest, result.getTotalElements());
    }




}
