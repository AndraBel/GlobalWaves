package app;

import app.audioFiles.AudioFiles;
import app.audioFiles.Song;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.podcasts.Podcast;
import app.users.Artist;

import java.util.*;
import java.util.stream.Collectors;

public class UsersHistory {
//    private LinkedList<Song> listenedSongs;
//    private LinkedList<Album> listenedAlbums;
//    private LinkedList<Podcast> listenedPodcasts;
//
//    public UsersHistory() {
//        listenedSongs = new LinkedList<>();
//        listenedAlbums = new LinkedList<>();
//        listenedPodcasts = new LinkedList<>();
//    }

    private LinkedHashMap<Song, Integer> listenedSongs;
    private LinkedHashMap<Album, Integer> listenedAlbums;
    private LinkedHashMap<Podcast, Integer> listenedPodcasts;
    private LinkedHashMap<String, Integer> genres;
    private LinkedHashMap<String, Integer> artists;

    public UsersHistory() {
        listenedSongs = new LinkedHashMap<>();
        listenedAlbums = new LinkedHashMap<>();
        listenedPodcasts = new LinkedHashMap<>();
        genres = new LinkedHashMap<>();
        artists = new LinkedHashMap<>();
    }

    public void addSong(Song song) {
        if (listenedSongs.containsKey(song)) {
            // If the song is already in the map, increment the count
            int count = listenedSongs.get(song);
            listenedSongs.replace(song, count + 1);
        }  else {
            // If the song is not in the map, add it with a count of 1
            listenedSongs.put(song, 1);
        }
    }

    public void addAlbum(Album album) {
        if (listenedAlbums.containsKey(album)) {
            int count = listenedAlbums.get(album);
            listenedAlbums.replace(album, count + 1);
        } else {
            listenedAlbums.put(album, 1);
        }
    }

    public void calculateAlbumStatus () {
        for (Map.Entry<Album, Integer> entry : listenedAlbums.entrySet()) {
            Album album = entry.getKey();
            int total = 0;
            for(Song song : listenedSongs.keySet()) {
                if(song.getAlbum().equals(album.getName())) {
                    total += listenedSongs.get(song);
                }
            }
            listenedAlbums.replace(album, total);
        }
    }

    public void addPodcast(Podcast podcast) {
        if (listenedPodcasts.containsKey(podcast)) {
            int count = listenedPodcasts.get(podcast);
            listenedPodcasts.put(podcast, count + 1);
        } else {
            listenedPodcasts.put(podcast, 1);
        }
    }

    public void addGenres(String genre) {
        if (genres.containsKey(genre)) {
            int count = genres.get(genre);
            genres.put(genre, count + 1);
        } else {
            genres.put(genre, 1);
        }
    }

    public void getGenres () {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            String genre = entry.getKey().getGenre();
            addGenres(genre);
        }
    }

    public void addArtists(String artist) {
        if (artists.containsKey(artist)) {
            int count = artists.get(artist);
            artists.put(artist, count + 1);
        } else {
            artists.put(artist, 1);
        }
    }

    public void getArtists () {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            String artist = entry.getKey().getArtist();
            addArtists(artist);
        }
        for (Map.Entry<Album, Integer> entry : listenedAlbums.entrySet()) {
            String artist = entry.getKey().getArtist();
            addArtists(artist);
        }
    }

    public List<Map.Entry<Song, Integer>> getTopSongs() {
        return getTopEntries(listenedSongs);
    }

    public List<Map.Entry<Album, Integer>> getTopAlbums() {
        calculateAlbumStatus();
        return getTopEntries(listenedAlbums);
    }

    public List<Map.Entry<Podcast, Integer>> getTopPodcasts() {
        return getTopEntries(listenedPodcasts);
    }

    public List<Map.Entry<String, Integer>> getTopGenres() {
        getGenres();
        return getTopEntriesStrings(genres);
    }

    public List<Map.Entry<String, Integer>> getTopArtists() {
        getArtists();
        return getTopEntriesStrings(artists);
    }

    private static <T> List<Map.Entry<T, Integer>> getTopEntriesStrings(LinkedHashMap<T, Integer> map) {
        return map.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    // Sort by count in descending order
                    int countComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (countComparison != 0) {
                        return countComparison;
                    }
                    // If counts are equal, sort lexicographically by key
                    return entry1.getKey().toString().compareTo(entry2.getKey().toString());
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    private static <T extends AudioFiles> List<Map.Entry<T, Integer>> getTopEntries(LinkedHashMap<T, Integer> map) {
        return map.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    // Sort by count in descending order
                    int countComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (countComparison != 0) {
                        return countComparison;
                    }
                    // If counts are equal, sort lexicographically by key
                    return entry1.getKey().getName().compareTo(entry2.getKey().getName());
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    public LinkedHashMap<Song, Integer> getListenedSongs() {
        return listenedSongs;
    }
}
