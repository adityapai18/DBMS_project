package model;

/** Author corresponds to a {@code PERSON} who writes articles or chapters. */
public class Author {
    private int personId;
    private String name;

    public Author() {}

    public Author(int personId, String name) {
        this.personId = personId;
        this.name = name;
    }

    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
