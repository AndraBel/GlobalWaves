package app.audioFiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonCreator
    public Song(@JsonProperty("name") final String name,
                @JsonProperty("duration") final Integer duration,
                @JsonProperty("album") final String album,
                @JsonProperty("tags") final ArrayList<String> tags,
                @JsonProperty("lyrics") final String lyrics,
                @JsonProperty("genre") final String genre,
                @JsonProperty("releaseYear") final Integer releaseYear,
                @JsonProperty("artist") final String artist) {
        this.name = name;
        this.duration = duration;
        this.album = album;
        this.tags = tags;
        this.lyrics = lyrics;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.artist = artist;
        likes = 0;
        listeners = 0;
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

    /**
     * @return the artist of the song
     */
    public String getArtist() {
        return artist;
    }
}
