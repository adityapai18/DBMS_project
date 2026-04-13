package model;

import java.sql.Date;

public class Article {
    private int issueId;
    private int articleNumber;
    private String title;
    private int personId;
    private String topic;
    private Date dateWritten;
    private String fullText;

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }
    public int getArticleNumber() { return articleNumber; }
    public void setArticleNumber(int articleNumber) { this.articleNumber = articleNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public Date getDateWritten() { return dateWritten; }
    public void setDateWritten(Date dateWritten) { this.dateWritten = dateWritten; }
    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }
}
