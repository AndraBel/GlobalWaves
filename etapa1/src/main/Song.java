package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a song
 */
public class Song extends AudioFiles {
    private final int duration;
    private final String album;
    private final ArrayList<String> tags;
    private final String lyrics;
    private final String genre;
    private final int releaseYear;
    private final String artist;
    private int likes;

    public Song(final String name, final int duration, final String album,
                final ArrayList<String> tags, final String lyrics, final String genre,
                final int releaseYear, final String artist) {
        this.name = name;
        this.duration = duration;
        this.album = album;
        this.tags = tags;
        this.lyrics = lyrics;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.artist = artist;
        likes = 0;
    }

    /**
     * @param filters HashMap of filters to be applied
     * @return true if the song matches all the filters, false otherwise
     */
    @Override
    public boolean matchFilters(final HashMap<String, Object> filters) {
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            switch (filter.getKey()) {
                case "name":
                    if (!this.name.startsWith((String) filter.getValue())) {
                        return false;
                    }
                    break;
                case "album":
                    if (!this.album.equals((String) filter.getValue())) {
                        return false;
                    }
                    break;
                case "tags":
                    if (!this.tags.containsAll((ArrayList<String>) filter.getValue())) {
                        return false;
                    }
                    break;
                case "lyrics":
                    if (!this.lyrics.toLowerCase().contains(
                            ((String) filter.getValue()).toLowerCase())) {
                        return false;
                    }
                    break;
                case "genre":
                    if (!(this.genre).equalsIgnoreCase(((String) filter.getValue()))) {
                        return false;
                    }
                    break;
                case "releaseYear":
                    final int year = Integer.parseInt(
                            ((String) filter.getValue()).substring(1));
                    if (((String) filter.getValue()).startsWith("<")) {
                        if (this.releaseYear >= year) {
                            return false;
                        }
                    } else if (((String) filter.getValue()).startsWith(">")) {
                        if (this.releaseYear <= year) {
                            return false;
                        }
                    }
                    break;
                case "artist":
                    if (!this.artist.equals((String) filter.getValue())) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * This method increases the number of likes of the song
     */
    public void likeSong() {
        likes++;
    }

    /**
     * This method decreases the number of likes of the song
     */
    public void unlikeSong() {
        likes--;
    }

    /**
     * @return the duration of the song
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return the number of likes of the song
     */
    public int getLikes() {
        return likes;
    }
}
