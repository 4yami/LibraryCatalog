package com.mycompany.librarycatalog;

public class Book {

    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int year;
    private String genre;
    private int copyId;
    private double price;
    private String language;

    // Constructor that matches the fields you're passing
    public Book(int bookId, String isbn, String title, String author, String publisher,
                int year, String genre, int copyId, double price, String language) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.genre = genre;
        this.copyId = copyId;
        this.price = price;
        this.language = language;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getCopyId() { return copyId; }
    public void setCopyId(int copyId) { this.copyId = copyId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
