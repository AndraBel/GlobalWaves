package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchBar {
    private ArrayList<Song> songs;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Playlist> publicPlaylists;
    private ArrayList<Playlist> userPlaylists;

    public SearchBar(ArrayList<Song> songs, ArrayList<Podcast> podcasts, ArrayList<Playlist> publicPlaylists, ArrayList<Playlist> userPlaylists) {
        this.songs = songs;
        this.podcasts = podcasts;
        this.publicPlaylists = publicPlaylists;
        this.userPlaylists = userPlaylists;
    }

    public ArrayList<String> Search(Command command) {
        ArrayList<String> result = new ArrayList<>();
        int index = 0;
        if (command.getType().equals("song")) {
            HashMap<String, Object> filters = command.getFilters();
            for (Song song : songs) {
                if (song.matchFilters(filters)) {
                    result.add(song.getName());
                    index++;
                    if (index == 5) {
                        break;
                    }
                }
            }

        } else if (command.getType().equals("podcast")) {
            HashMap<String, Object> filters = command.getFilters();
            for (Podcast podcast : podcasts) {
                if (podcast.matchFilters(filters)) {
                    result.add(podcast.getName());
                    index++;
                    if (index == 5) {
                        break;
                    }
                }
            }
        } else if (command.getType().equals("playlist")) {
            HashMap<String, Object> filters = command.getFilters();
            for (Playlist playlist : userPlaylists) {
                if (playlist.matchFilters(filters)) {
                    result.add(playlist.getName());
                    index++;
                    if (index == 5) {
                        return result;
                    }
                }
            }
            for (Playlist playlist : publicPlaylists) {
                if (playlist.matchFilters(filters)) {
                    if (!result.contains(playlist.getName())) {
                        result.add(playlist.getName());
                        index++;
                        if (index == 5) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

}
