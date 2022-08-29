package br.com.carv.apitdd.model.dto;

public class LoanDTOBuilder {

    private LoanDTO loanDTO;

    public LoanDTOBuilder() {
        loanDTO = new LoanDTO();
    }

    public static LoanDTOBuilder builder() {
        return new LoanDTOBuilder();
    }

    public LoanDTOBuilder isbn(String isbn) {
        this.loanDTO.setIsbn(isbn);
        return this;
    }

    public  LoanDTOBuilder customer(String customer) {
        this.loanDTO.setCustomer(customer);
        return this;
    }

    public LoanDTOBuilder customerEmail(String email) {
        this.loanDTO.setCustomerEmail(email);
        return this;
    }

    public LoanDTO build() {
        return this.loanDTO;
    }
}
