package main.Users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.*;
import main.UserPages.ArtistPage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class Artist {
    private String name;
    private LinkedHashMap<String, Album> albums;
    private ArrayList<Album> allAlbums;
    private ArrayList<Event> events;
    private ArrayList<Event> allEvents;
    private ArrayList<Merch> merch;
    private ArrayList<Merch> allMerch;
    private ArtistPage artistPage;

    public Artist(final ArrayList<Album> allAlbums,
                  final ArrayList<Event> allEvents,
                  final ArrayList<Merch> allMerch, final String username) {
//        super(username, age, city, songs, podcasts, allPlaylists, allAlbums);
        albums = new LinkedHashMap<>();
        this.allAlbums = allAlbums;
        events = new ArrayList<>();
        this.allEvents = allEvents;
        merch = new ArrayList<>();
        this.allMerch = allMerch;
        artistPage = new ArtistPage(albums, events, merch);
        name = username;
    }

    public static boolean hasDuplicateSongs(ArrayList<Song> songs) {
        Set<String> songSet = new HashSet<>();

        for (Song song : songs) {
            // If the song is already in the set, it's a duplicate
            if (!songSet.add(song.getName())) {
                return true; // Duplicate found
            }
        }

        return false; // No duplicates found
    }

    public Integer addAlbum(Album album) {
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

    public Integer addEvent(Event event, ObjectNode resultNode) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            // Parse the string to a LocalDate object
            LocalDate date = LocalDate.parse(event.getDate(), formatter);

            // Extract day, month, and year as integers
            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();

            // Check for February days
            if (month == 2 && (day < 1 || day > 28)) {
                return 0;
            }

            // Check month range
            if (month < 1 || month > 12) {
                return 0;
            }

            // Check year range
            if (year < 1900 || year > 2023) {
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
        allEvents.add(event);
        return 2;
    }

    public Integer addMerch(final Merch newMerch, final ObjectNode resultNode) {
        for (Merch merch : this.merch) {
            if (merch.getName().equals(newMerch.getName())) {
                return 0;
            }
        }
        if (newMerch.getPrice() < 0) {
            return 1;
        }
        merch.add(newMerch);
        allMerch.add(newMerch);
        return 2;
    }

    public Integer removeAlbum (final String albumName, Library library) {
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
        allAlbums.remove(albumName);
        return 2;
    }

    public Integer removeEvent (final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                events.remove(event);
                allEvents.remove(event);
                return 1;
            }
        }
        return 0;
    }

    public Integer getAllLikes() {
        int totalLikes = 0;
        for (Album album : albums.values()) {
            totalLikes += album.getAllLikes();
        }
        return totalLikes;
    }

    public LinkedHashMap<String, Album> getAlbums() {
        return albums;
    }

    public ArtistPage getArtistPage() {
        return artistPage;
    }

    public String getName() {
        return name;
    }
}
