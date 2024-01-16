package app.users;

import app.users.user.Observer;

public interface Subject {
    /**
     * Registers an observer to the list of observers.
     *
     * @param o The Observer object to be registered.
     */
    void registerObserver(Observer o);
    /**
     * Removes an observer from the list of observers.
     *
     * @param o The Observer object to be removed.
     */
    void removeObserver(Observer o);
    /**
     * Notifies all registered observers with a given notification and description.
     *
     * @param notification The notification message to be sent to observers.
     * @param description  A description accompanying the notification.
     */
    void notifyObservers(String notification, String description);
}
