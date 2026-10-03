package model;

public class Project {

    private int id;
    private String title;
    private String description;
    private String requiredSkill;
    private int createdBy;

    public Project() {
    }

    public Project(
            String title,
            String description,
            String requiredSkill,
            int createdBy
    ) {
        this.title = title;
        this.description = description;
        this.requiredSkill = requiredSkill;
        this.createdBy = createdBy;
    }

    public Project(
            int id,
            String title,
            String description,
            String requiredSkill,
            int createdBy
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkill = requiredSkill;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    public void setRequiredSkill(String requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}