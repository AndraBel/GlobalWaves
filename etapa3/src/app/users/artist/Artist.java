package app.users.artist;

import app.admin.Library;
import app.users.Subject;
import app.users.user.Observer;
import app.users.user.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.Song;
import app.userPages.ArtistPage;
import app.users.userComponents.publicity.Event;
import app.users.userComponents.publicity.Merch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Artist implements Subject {
    private String name;
    private LinkedHashMap<String, Album> albums;
    private ArrayList<Album> allAlbums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merch;
    private ArtistPage artistPage;
    private boolean hasBeenListenedTo;
    private static final int MONTH_NR = 2;
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2023;
    private static final int MAX_DAYS = 2023;
    private Integer listeners;
    private ArrayList<User> listenersList;
    private double songRevenue;
    private List<Observer> observers;


    public Artist(final ArrayList<Album> allAlbums, final String username) {
        albums = new LinkedHashMap<>();
        this.allAlbums = allAlbums;
        events = new ArrayList<>();
        merch = new ArrayList<>();
        artistPage = new ArtistPage(albums, events, merch, username);
        name = username;
        hasBeenListenedTo = false;
        listeners = 0;
        listenersList = new ArrayList<>();
        songRevenue = 0;
        observers = new ArrayList<>();
    }

    /**
     * Checks if there are any duplicates in the songs array
     *
     * @param songs The array of songs to check
     * @return true if there are duplicates, false otherwise
     */
    private static boolean hasDuplicateSongs(final ArrayList<Song> songs) {
        Set<String> songSet = new HashSet<>();

        for (Song song : songs) {
            // If the song is already in the set, it's a duplicate
            if (!songSet.add(song.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds an album to the artist's albums
     *
     * @param album The album to add
     * @return 0 if the album already exists, 1 if the album has duplicate songs,
     * 2 if the album was successfully added
     */
    public Integer addAlbum(final Album album) {
        if (albums.containsKey(album.getName())) {
            return 0;
        }
        if (hasDuplicateSongs(album.getSongs())) {
            // has duplicate songs
            return 1;
        }
        albums.put(album.getName(), album);
        allAlbums.add(album);
        return 2;
    }

    /**
     * Adds an event to the artist's events
     *
     * @param event      The event to add
     * @param resultNode The JSON object to write the result to
     * @return 0 if the date is invalid, 1 if the event already exists,
     * 2 if the event was successfully added
     */
    public Integer addEvent(final Event event, final ObjectNode resultNode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            // Parse the string to a LocalDate object
            LocalDate date = LocalDate.parse(event.getDate(), formatter);

            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();

            // Check for February days
            if (month == MONTH_NR && day > MAX_DAYS) {
                return 0;
            }

            // Check year range
            if (year < MIN_YEAR || year > MAX_YEAR) {
                return 0;
            }

        } catch (DateTimeParseException e) {
            return 0;
        }

        for (Event e : events) {
            if (e.getName().equals(event.getName())) {
                return 1;
            }
        }
        events.add(event);
        return 2;
    }

    /**
     * Adds a merch to the artist's merch
     *
     * @param newMerch   The merch to add
     * @param resultNode The JSON object to write the result to
     * @return 0 if the merch already exists, 1 if the price is invalid,
     * 2 if the merch was successfully added
     */
    public Integer addMerch(final Merch newMerch, final ObjectNode resultNode) {
        for (Merch merch1 : merch) {
            if (merch1.getName().equals(newMerch.getName())) {
                return 0;
            }
        }
        if (newMerch.getPrice() < 0) {
            return 1;
        }
        merch.add(newMerch);
        return 2;
    }

    /**
     * Removes an album from the artist's albums
     *
     * @param albumName The name of the album to remove
     * @param library   The library to remove the songs from
     * @return 0 if the album doesn't exist, 1 if the album has listeners,
     * 2 if the album was successfully removed
     */
    public Integer removeAlbum(final String albumName, final Library library) {
        if (!albums.containsKey(albumName)) {
            return 0;
        }
        Album album = albums.get(albumName);

        for (Song song : album.getSongs()) {
            if (song.getListeners() > 0) {
                return 1;
            }
        }
        if (album.getListeners() > 0) {
            return 1;
        }

        for (Song song : album.getSongs()) {
            library.getSongs().remove(song);
        }

        albums.remove(albumName);

        for (Album album1 : allAlbums) {
            if (album1.getName().equals(albumName)) {
                allAlbums.remove(album1);
                break;
            }
        }
        return 2;
    }

    /**
     * Removes an event from the artist's events
     *
     * @param eventName The name of the event to remove
     * @return 0 if the event doesn't exist, 1 if the event was successfully removed
     */
    public Integer removeEvent(final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                events.remove(event);
                return 1;
            }
        }
        return 0;
    }

    /**
     * @return the number of likes of all the albums for an artist
     */
    public Integer getAllLikes() {
        int totalLikes = 0;
        for (Album album : albums.values()) {
            totalLikes += album.getTotalLikes();
        }
        return totalLikes;
    }

    /**
     * Adds revenue to the total song revenue.
     *
     * @param revenue The amount of revenue to be added.
     */
    public void addSongRevenue(final double revenue) {
        songRevenue += revenue;
    }

    /**
     * Marks the entity as having been listened to.
     */
    public void setHasBeenListenedTo() {
        hasBeenListenedTo = true;
    }

    /**
     * Increments the count of listeners.
     */
    public void increaseListeners() {
        listeners++;
    }


    /**
     * Adds a user to the list of listeners if they are not already in the list.
     * This method is used to track users who have listened to a particular entity,
     * such as a song or album.
     *
     * @param user The User object to be added to the list of listeners.
     */
    public void addListener(final User user) {
        if (listenersList.contains(user)) {
            return;
        }
        listenersList.add(user);
    }

    /**
     * Registers an observer to the list of observers.
     * If the observer is already registered, it is first removed before being re-added,
     * ensuring it's only registered once.
     *
     * @param o The Observer object to be registered.
     */
    @Override
    public void registerObserver(final Observer o) {
        if (observers.contains(o)) {
            observers.remove(o);
        }
        observers.add(o);
    }

    /**
     * Removes an observer from the list of observers.
     * This method is used to stop notifying the observer about changes or updates.
     *
     * @param o The Observer object to be removed.
     */
    @Override
    public void removeObserver(final Observer o) {
        observers.remove(o);
    }

    /**
     * Notifies all registered observers with a given notification and description.
     * This method is used to send updates or notifications to all observers.
     *
     * @param notification The notification message to be sent to observers.
     * @param description  A description accompanying the notification.
     */
    @Override
    public void notifyObservers(final String notification, final String description) {
        for (Observer observer : observers) {
            observer.update(notification, description);
        }
    }


    /**
     * @return a hashmap of the artist's albums
     */
    public LinkedHashMap<String, Album> getAlbums() {
        return albums;
    }

    /**
     * @return the artist's page
     */
    public ArtistPage getArtistPage() {
        return artistPage;
    }

    /**
     * @return the artist's name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the entity (such as a song or album) has been listened to.
     *
     * @return boolean True if the entity has been listened to, false otherwise.
     */
    public boolean getHasBeenListenedTo() {
        return hasBeenListenedTo;
    }

    /**
     * Retrieves the number of listeners for the entity.
     *
     * @return Integer representing the total number of listeners.
     */
    public Integer getListeners() {
        return listeners;
    }

    /**
     * Retrieves the list of users who are listeners of the entity.
     *
     * @return ArrayList<User> containing the list of listener users.
     */
    public ArrayList<User> getListenersList() {
        return listenersList;
    }

    /**
     * Gets the total revenue generated by the entity, such as a song.
     *
     * @return double representing the total revenue generated by the entity.
     */
    public double getSongRevenue() {
        return songRevenue;
    }

}
