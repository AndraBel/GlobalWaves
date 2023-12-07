package main;

public class Announcement {
    private final String name;
    private final String description;
    private final String owner;

    public Announcement(final String name, final String description, final String owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
