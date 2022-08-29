package br.com.carv.apitdd.controller;

import br.com.carv.apitdd.exception.ApiErrors;
import br.com.carv.apitdd.exception.BusinessException;
import br.com.carv.apitdd.model.Book;
import br.com.carv.apitdd.model.Loan;
import br.com.carv.apitdd.model.dto.BookDTO;
import br.com.carv.apitdd.model.dto.LoanDTO;
import br.com.carv.apitdd.service.BookService;
import br.com.carv.apitdd.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private ModelMapper modelMapper;

    private final LoanService loanService;

    public BookController(BookService bookService, ModelMapper modelMapper, LoanService loanService) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        book = bookService.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable ("id") Long id) {
        Book book = bookService.getById(id).orElseThrow(() -> new BusinessException("Book not found! Id: " + id));
        bookService.delete(book);

    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO get(@PathVariable ("id") Long id) {
        return bookService.getById(id).map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new BusinessException("Book not found! Id: " + id ));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BookDTO update(@PathVariable ("id") Long id, @RequestBody BookDTO bookDTO) {
        Book book = bookService.getById(id).orElseThrow(() -> new BusinessException("Book not found! Id: " + id));
        book.setAuthor(bookDTO.getAuthor());
        book.setTitle(bookDTO.getTitle());
        book = bookService.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> find(@RequestBody BookDTO bookDTO, Pageable pageable) {
        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book> result = bookService.find(filter, pageable);
        List<BookDTO> collect = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class)).collect(Collectors.toList());

        return new PageImpl<BookDTO>(collect, pageable, result.getTotalElements());

    }

    @GetMapping("/{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable("id") Long id, Pageable pageable) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);

        List<LoanDTO> collect = result.getContent().stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(collect, pageable, result.getTotalElements());
    }
}

