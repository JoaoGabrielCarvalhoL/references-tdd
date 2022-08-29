package br.com.carv.apitdd.model.dto;

import javax.validation.constraints.NotEmpty;

public class LoanDTO {

    @NotEmpty
    private String isbn;
    @NotEmpty
    private String customer;

    private Long id;

    private BookDTO book;

    @NotEmpty
    private String customerEmail;

    public LoanDTO() {

    }

    public LoanDTO(String isbn, String customer) {
        this.isbn = isbn;
        this.customer = customer;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String email) {
        this.customerEmail = email;
    }


}
