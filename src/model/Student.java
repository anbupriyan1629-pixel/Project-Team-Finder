package model;

public class Student {

    private int id;
    private String name;
    private String email;
    private String skill;
    private String availability;

    // Empty constructor
    public Student() {
    }

    // Constructor without ID
    public Student(String name, String email, String skill, String availability) {
        this.name = name;
        this.email = email;
        this.skill = skill;
        this.availability = availability;
    }

    // Constructor with ID
    public Student(int id, String name, String email, String skill, String availability) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.skill = skill;
        this.availability = availability;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSkill() {
        return skill;
    }

    public String getAvailability() {
        return availability;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}