package br.com.carv.apitdd.service.impl;

import br.com.carv.apitdd.exception.BusinessException;
import br.com.carv.apitdd.model.BookBuilder;
import br.com.carv.apitdd.model.Loan;
import br.com.carv.apitdd.model.LoanBuilder;
import br.com.carv.apitdd.repository.LoanRepository;
import br.com.carv.apitdd.service.LoanService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceImplTest {

    @MockBean
    private LoanRepository loanRepository;

    @MockBean
    private LoanService loanService;

    @Test
    public void should_save_loan() {
        Loan loan = LoanBuilder.builder()
                .book(BookBuilder.builder().id(1L).build())
                .customer("Joao Gabriel")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = LoanBuilder.builder()
                .id(1L)
                .book(BookBuilder.builder().id(1L).build())
                .customer("Joao Gabriel")
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.existsByBookAndNotReturned(BookBuilder.builder().id(1L).build())).thenReturn(false);
        Mockito.when(loanRepository.save(loan)).thenReturn(savedLoan);

        loan = loanService.save(loan);

        Assertions.assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    public void should_throw_exception_when_save_loan() {
        Loan loan = LoanBuilder.builder()
                .book(BookBuilder.builder().id(1L).build())
                .customer("Joao Gabriel")
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.existsByBookAndNotReturned(BookBuilder.builder().id(1L).build())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> loanService.save(loan));

        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(loanRepository, Mockito.never()).save(loan);
    }

    @Test
    public void should_return_loan_by_id() {

        Loan loan = LoanBuilder.builder()
                .id(1L)
                .book(BookBuilder.builder().id(1L).build())
                .customer("Joao Gabriel")
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(loan));

        Optional<Loan> optionalLoan = loanService.getById(Long.valueOf(1));

        Assertions.assertThat(optionalLoan.isPresent()).isTrue();
        Assertions.assertThat(optionalLoan.get().getId()).isEqualTo(loan.getId());
        Assertions.assertThat(optionalLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(optionalLoan.get().getBook().getId()).isEqualTo(loan.getBook().getId());
        Assertions.assertThat(optionalLoan.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(loanRepository).findById(loan.getId());
    }

    @Test
    public void should_update_loan() {

        Loan loan = LoanBuilder.builder()
                .id(1L)
                .book(BookBuilder.builder().id(1L).build())
                .customer("Joao Gabriel")
                .returned(true)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.save(loan)).thenReturn(loan);

        Loan updatedLoan = loanService.update(loan);

        Assertions.assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(loanRepository).save(loan);
    }

}
