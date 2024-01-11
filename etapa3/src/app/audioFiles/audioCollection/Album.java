package app.audioFiles.audioCollection;

import app.audioFiles.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Album extends AudioFilesCollection {
    private final Integer releaseYear;
    private final String description;

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
                    if (!name.toLowerCase().startsWith(((String) filter.getValue()).toLowerCase())) {
                        return false;
                    }
                    break;
                case "owner":
                    if (!owner.equalsIgnoreCase((String) filter.getValue())) {
                        return false;
                    }
                    break;
                case "description":
                    if (!description.equalsIgnoreCase((String) filter.getValue())) {
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
     * @return the array of songs in the album
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     * @return the release year of the album
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * @return the the description of the album
     */
    public String getDescription() {
        return description;
    }

    public String getArtist() {
        return owner;
    }
}
