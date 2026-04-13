package model;

import java.sql.Date;

public class Issue {
    private int issueId;
    private int publicationId;
    private String issueTitle;
    private Date publicationDate;

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public int getPublicationId() { return publicationId; }
    public void setPublicationId(int publicationId) { this.publicationId = publicationId; }
    public String getIssueTitle() { return issueTitle; }
    public void setIssueTitle(String issueTitle) { this.issueTitle = issueTitle; }
    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
}
