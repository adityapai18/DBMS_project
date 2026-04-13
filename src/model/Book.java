package model;

import java.sql.Date;

/** Represents a row in {@code BOOK_EDITION}. */
public class Book {
    private int editionId;
    private int publicationId;
    private int editionNumber;
    private String isbn;
    private Date publicationDate;

    public int getEditionId() { return editionId; }
    public void setEditionId(int editionId) { this.editionId = editionId; }
    public int getPublicationId() { return publicationId; }
    public void setPublicationId(int publicationId) { this.publicationId = publicationId; }
    public int getEditionNumber() { return editionNumber; }
    public void setEditionNumber(int editionNumber) { this.editionNumber = editionNumber; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
}
