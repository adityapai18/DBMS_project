package model;

public class Publication {
    private int publicationId;
    private String title;
    private String pubType;
    private String periodicity;

    public Publication() {}

    public Publication(int publicationId, String title, String pubType, String periodicity) {
        this.publicationId = publicationId;
        this.title = title;
        this.pubType = pubType;
        this.periodicity = periodicity;
    }

    public int getPublicationId() { return publicationId; }
    public void setPublicationId(int publicationId) { this.publicationId = publicationId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPubType() { return pubType; }
    public void setPubType(String pubType) { this.pubType = pubType; }
    public String getPeriodicity() { return periodicity; }
    public void setPeriodicity(String periodicity) { this.periodicity = periodicity; }
}
