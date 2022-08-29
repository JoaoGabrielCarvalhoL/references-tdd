package br.com.carv.apitdd.model.dto;

public class LoanFilterDTO {

    private String isbn;
    private String customer;

    public LoanFilterDTO() {

    }

    public LoanFilterDTO(String isbn, String customer) {
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
}
