package app.users.userComponents.publicity;

public class Event extends Publicity {
    private final String date;

    public Event(final String owner, final String name, final String description,
                 final String date) {
        super(owner, name, description);
        this.date = date;
    }

    /**
     * @return the date of the event
     */
    public String getDate() {
        return date;
    }
}
