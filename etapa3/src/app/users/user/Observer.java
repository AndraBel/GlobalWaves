package app.users.user;

public interface Observer {
    /**
     * Updates the observer with a given notification and description.
     *
     * @param notification The notification message to be sent to observers.
     * @param description  A description accompanying the notification.
     */
    void update(String notification, String description);
}
