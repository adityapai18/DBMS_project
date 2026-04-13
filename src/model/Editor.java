package model;

/** Editor is a {@code PERSON} linked to publications via {@code ASSIGNED_TO}. */
public class Editor {
    private int personId;
    private String name;

    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
