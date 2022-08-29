package br.com.carv.apitdd.model.dto;

public class ReturnedLoadDTO {

    private Boolean returned;

    public ReturnedLoadDTO() {

    }

    public ReturnedLoadDTO(Boolean returned) {
        this.returned = returned;
    }

    public Boolean getReturned() {
        return returned;
    }

    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

}
