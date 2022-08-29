package br.com.carv.apitdd.service;

import br.com.carv.apitdd.model.Book;
import br.com.carv.apitdd.model.Loan;
import br.com.carv.apitdd.model.dto.LoanDTO;
import br.com.carv.apitdd.model.dto.LoanFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO dto, Pageable pageable);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}
