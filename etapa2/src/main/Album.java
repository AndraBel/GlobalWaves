package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Album extends AudioFiles {
    private final String name;
    private final String owner;
    private final Integer releaseYear;
    private final String description;
    private ArrayList<Song> songs;
    private Integer listeners;

    public Album(final String name, final String owner, final Integer releaseYear,
                 final String description, final ArrayList<Song> songs) {
        this.name = name;
        this.owner = owner;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
        listeners = 0;
    }

    /**
     * @param filters HashMap of filters to be applied
     * @return true if the audio file matches all the filters, false otherwise
     */
    @Override
    public boolean matchFilters(final HashMap<String, Object> filters) {
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            switch (filter.getKey()) {
                case "name":
                    if (!name.startsWith((String) filter.getValue())) {
                        return false;
                    }
                    break;
                case "owner":
                    if (!owner.equals((String) filter.getValue())) {
                        return false;
                    }
                    break;
                case "description":
                    if (!description.equals((String) filter.getValue())) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    public Integer getAllLikes() {
        int totalLikes = 0;
        for (Song song : songs) {
            totalLikes += song.getLikes();
        }
        return totalLikes;
    }

    public void increaseListeners() {
        listeners++;
    }

    public void decreaseListeners() {
        listeners--;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public Integer getListeners() {
        return listeners;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public String getDescription() {
        return description;
    }
}
