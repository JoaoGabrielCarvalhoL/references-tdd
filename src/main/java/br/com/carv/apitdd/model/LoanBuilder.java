package br.com.carv.apitdd.model;

import java.time.LocalDate;

public class LoanBuilder {

    private Loan loan;

    public LoanBuilder() {
        loan = new Loan();
    }

    public static LoanBuilder builder() {
        return new LoanBuilder();
    }

    public LoanBuilder id(Long id) {
        this.loan.setId(id);
        return this;
    }

    public LoanBuilder customer(String customer) {
        this.loan.setCustomer(customer);
        return this;
    }

    public LoanBuilder book(Book book) {
        this.loan.setBook(book);
        return this;
    }

    public LoanBuilder loanDate(LocalDate loanDate) {
        this.loan.setLoanDate(loanDate);
        return this;
    }

    public LoanBuilder returned(Boolean returned) {
        this.loan.setReturned(returned);
        return this;
    }

    public LoanBuilder customerEmail(String email) {
        this.loan.setCustomerEmail(email);
        return this;
    }

    public Loan build() {
        return this.loan;
    }
}
