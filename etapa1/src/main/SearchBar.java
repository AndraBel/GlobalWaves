package main;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBar {
    private final ArrayList<Song> songs;
    private final ArrayList<Podcast> podcasts;
    private final ArrayList<Playlist> allPlaylists;
    private final ArrayList<Playlist> userPlaylists;
    private static final int MAX_SEARCH_RESULTS = 5;

    public SearchBar(final ArrayList<Song> songs,
                     final ArrayList<Podcast> podcasts,
                     final ArrayList<Playlist> allPlaylists,
                     final ArrayList<Playlist> userPlaylists) {
        this.songs = songs;
        this.podcasts = podcasts;
        this.allPlaylists = allPlaylists;
        this.userPlaylists = userPlaylists;
    }

    /**
     * A generic method to filter a list of audio files based on specified filters
     *
     * @param audioFiles ArrayList of audio files to be searched
     * @param filters    HashMap of filters to be applied
     * @param result     ArrayList of names of audio files that match the filters
     * @param <K>        type of audio files
     */
    private static <K extends AudioFiles> void matchFilters(final ArrayList<K> audioFiles,
                                                            final HashMap<String, Object> filters,
                                                            final ArrayList<String> result) {
        int index = 0;
        for (K audioFile : audioFiles) {
            if (audioFile.matchFilters(filters)) {
                result.add(audioFile.getName());
                index++;
                if (index == MAX_SEARCH_RESULTS) {
                    break;
                }
            }
        }
    }

    /**
     * This method searches for audio files that match the filters
     *
     * @param command Command to be searched
     * @return ArrayList of names of audio files that match the filters
     */
    public ArrayList<String> search(final Command command) {
        ArrayList<String> result = new ArrayList<>();
        switch (command.getType()) {
            case "song" -> {
                matchFilters(songs, command.getFilters(), result);
            }
            case "podcast" -> {
                matchFilters(podcasts, command.getFilters(), result);
            }
            case "playlist" -> {
                matchFilters(userPlaylists, command.getFilters(), result);
                int index = 0;
                // Iterate through all playlists and add matching visible playlists to the result
                for (Playlist playlist : allPlaylists) {
                    if (playlist.matchFilters(command.getFilters()) && playlist.isVisibility()) {
                        if (!result.contains(playlist.getName())) {
                            result.add(playlist.getName());
                            index++;
                            if (index == MAX_SEARCH_RESULTS) {
                                break;
                            }
                        }
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + command.getType());
        }
        return result;
    }
}
