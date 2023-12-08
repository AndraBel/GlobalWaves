package app.users.publicity;

public class Publicity {
    private final String owner;
    private final String name;
    private final String description;

    public Publicity(final String owner, final String name, final String description) {
        this.owner = owner;
        this.name = name;
        this.description = description;
    }

    /**
     * @return the owner of the event/announcement/merch
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return the name of the event/announcement/merch
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description of the event/announcement/merch
     */
    public String getDescription() {
        return description;
    }
}
