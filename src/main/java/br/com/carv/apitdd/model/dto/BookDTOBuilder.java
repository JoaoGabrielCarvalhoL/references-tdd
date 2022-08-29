package br.com.carv.apitdd.model.dto;

public class BookDTOBuilder {

    private BookDTO bookDTO;

    public BookDTOBuilder() {
        bookDTO = new BookDTO();
    }
    public static BookDTOBuilder builder() {
        return new BookDTOBuilder();
    }
    public BookDTOBuilder id(Long id) {
        this.bookDTO.setId(id);
        return this;
    }
    public BookDTOBuilder title(String title) {
        this.bookDTO.setTitle(title);
        return this;
    }
    public BookDTOBuilder author(String author) {
        this.bookDTO.setAuthor(author);
        return this;
    }
    public BookDTOBuilder isbn(String isbn) {
        this.bookDTO.setIsbn(isbn);
        return this;
    }
    public BookDTO build() {
        return this.bookDTO;
    }
}
