package br.com.carv.apitdd.model.dto;

public class ReturnedLoadDTOBuilder {

    private ReturnedLoadDTO returnedLoadDTO;

    public ReturnedLoadDTOBuilder() {
        returnedLoadDTO = new ReturnedLoadDTO();
    }

    public static ReturnedLoadDTOBuilder builder() {
        return new ReturnedLoadDTOBuilder();
    }

    public ReturnedLoadDTOBuilder returned(Boolean returned) {
        this.returnedLoadDTO.setReturned(returned);
        return this;
    }

    public ReturnedLoadDTO build() {
        return this.returnedLoadDTO;
    }
}
