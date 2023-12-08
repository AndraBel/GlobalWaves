package app.users.userComponents;

import app.audioFiles.audioCollection.Album;
import app.audioFiles.AudioFiles;
import app.audioFiles.audioCollection.Playlist;
import app.audioFiles.Song;
import app.audioFiles.podcasts.Podcast;
import app.users.Artist;
import app.users.Host;
import app.admin.Command;
import app.admin.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchBar {
    private final ArrayList<Song> songs;
    private final ArrayList<Podcast> podcasts;
    private final ArrayList<Playlist> allPlaylists;
    private final ArrayList<Playlist> userPlaylists;
    private final ArrayList<Album> allAlbums;
    private static final int MAX_SEARCH_RESULTS = 5;

    public SearchBar(final ArrayList<Song> songs,
                     final ArrayList<Podcast> podcasts,
                     final ArrayList<Playlist> allPlaylists,
                     final ArrayList<Playlist> userPlaylists,
                     final ArrayList<Album> allAlbums) {
        this.songs = songs;
        this.podcasts = podcasts;
        this.allPlaylists = allPlaylists;
        this.userPlaylists = userPlaylists;
        this.allAlbums = allAlbums;
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
    public ArrayList<String> search(final Command command, final Library library) {
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
            case "album" -> {
                if (allAlbums.isEmpty()) {
                    System.out.println("No albums found");
                    break;
                }
                matchFilters(allAlbums, command.getFilters(), result);
            }
            case "artist" -> {
                int index = 0;
                for (Map.Entry<String, Artist> artist: library.getArtists().entrySet()) {
                    if (artist.getKey().startsWith((String) command.getFilters().get("name"))) {
                        if (!result.contains(artist.getKey())) {
                            result.add(artist.getKey());
                            index++;
                            if (index == MAX_SEARCH_RESULTS) {
                                break;
                            }
                        }
                    }
                }
            }
            case "host" -> {
                int index = 0;
                for (Map.Entry<String, Host> host: library.getHosts().entrySet()) {
                    if (host.getKey().startsWith((String) command.getFilters().get("name"))) {
                        if (!result.contains(host.getKey())) {
                            result.add(host.getKey());
                            index++;
                            if (index == MAX_SEARCH_RESULTS) {
                                break;
                            }
                        }
                    }
                }
            }
            default -> {
                System.out.println("Invalid type");
            }
        }
        return result;
    }
}
