package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Song {
    private String name;
    private int duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private int releaseYear;
    private String artist;
    private int likes;

    public Song(String name, int duration, String album, ArrayList<String> tags, String lyrics, String genre, int releaseYear, String artist) {
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

    public boolean matchFilters (HashMap<String, Object> filters) {
        for (Map.Entry<String, Object> filter: filters.entrySet()) {
            switch (filter.getKey()) {
                case "name":
                    if (!this.name.startsWith((String) filter.getValue()))
                        return false;
                    break;
                case "album":
                    if (!this.album.equals((String) filter.getValue()))
                        return false;
                    break;
                case "tags":
                    if (!this.tags.containsAll((ArrayList<String>)filter.getValue()))
                        return false;
                    break;
                case "lyrics":
                    if (!this.lyrics.toLowerCase().contains(((String) filter.getValue()).toLowerCase()))
                        return false;
                    break;
                case "genre":
                    if (!(this.genre).equalsIgnoreCase(((String) filter.getValue())))
                        return false;
                    break;
                case "releaseYear":
                    String releaseYear = (String) filter.getValue();
                    int year = Integer.parseInt(releaseYear.substring(1));
                    if (releaseYear.startsWith("<")) {
                        if (this.releaseYear >= year)
                            return false;
                    } else if (releaseYear.startsWith(">")) {
                        if (this.releaseYear <= year)
                            return false;
                    }
                    break;
                case "artist":
                    if (!this.artist.equals((String) filter.getValue()))
                        return false;
                    break;
            }
        }
        return true;
    }

    public void likeSong() {
        likes++;
    }
    public void unlikeSong() {
        likes--;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
