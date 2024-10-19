package Server;

public class Badge {
    private String title;
    private String description;

    public Badge(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return String.format("  - Title: %s\n  - Description: %s\n", this.title, this.description);
    }

}


