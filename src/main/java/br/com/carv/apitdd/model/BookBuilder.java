package br.com.carv.apitdd.model;

public class BookBuilder {
    private Book book;
    public BookBuilder() {
        book = new Book();
    }
    public static BookBuilder builder() {
        return new BookBuilder();
    }

    public BookBuilder id(Long id) {
        this.book.setId(id);
        return this;
    }
    public BookBuilder title(String title) {
        this.book.setTitle(title);
        return this;
    }
    public BookBuilder author(String author) {
        this.book.setAuthor(author);
        return this;
    }
    public BookBuilder isbn(String isbn) {
        this.book.setIsbn(isbn);
        return this;
    }
    public Book build() {
        return this.book;
    }

}
