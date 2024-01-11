package app.users.user;

import app.audioFiles.AudioFiles;
import app.audioFiles.Song;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.podcasts.Episode;
import app.audioFiles.podcasts.Podcast;
import app.users.userComponents.Player;

import java.util.*;
import java.util.stream.Collectors;

public class UsersHistory {
    private LinkedHashMap<Song, Integer> listenedSongs;
    private LinkedHashMap<Album, Integer> listenedAlbums;
    private LinkedHashMap<Podcast, Integer> listenedPodcasts;
    private LinkedHashMap<Episode, Integer> listenedEpisodes;
    private LinkedHashMap<String, Integer> genres;
    private ArrayList<Album> allAlbums;
    private Player player;
    private LinkedHashMap<Song, Integer> listenedSongsPremium;

    public UsersHistory(final ArrayList<Album> allAlbums) {
        listenedSongs = new LinkedHashMap<>();
        listenedAlbums = new LinkedHashMap<>();
        listenedPodcasts = new LinkedHashMap<>();
        listenedEpisodes = new LinkedHashMap<>();
        listenedSongsPremium = new LinkedHashMap<>();
        genres = new LinkedHashMap<>();
        this.allAlbums = allAlbums;
    }

    public void addSongPremium(Song song) {
        if (listenedSongsPremium.containsKey(song)) {
            // If the song is already in the map, increment the count
            int count = listenedSongsPremium.get(song);
            listenedSongsPremium.replace(song, count + 1);
        } else {
            listenedSongsPremium.put(song, 1);
        }
    }

    public int totalPremiumSongs() {
        int count = 0;
        for (Map.Entry<Song, Integer> entry : listenedSongsPremium.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    public void addSong(Song song) {
        if (listenedSongs.containsKey(song)) {
            // If the song is already in the map, increment the count
            int count = listenedSongs.get(song);
            listenedSongs.replace(song, count + 1);
        } else {
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

    public void displaySongs() {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            System.out.println(entry.getKey().getName() + " " + entry.getValue());
            System.out.println(entry.getKey().getArtist());
            System.out.println(entry.getKey().getGenre());
            System.out.println(entry.getKey().getAlbum());
            System.out.println("----------------------------------------");
            System.out.println();
        }
    }

    private Album findAlbum(Song song) {
        for (Album album : allAlbums) {
            if (album.getName().equals(song.getAlbum()) && album.getArtist().equals(song.getArtist())) {
                return album;
            }
        }
        return null;
    }

    private Album findAlbumByName(Song song) {
        for (Album album : allAlbums) {
            if (album.getName().equals(song.getAlbum())) {
                return album;
            }
        }
        return null;
    }

    public LinkedHashMap<Album, Integer> calculateAlbumStatus(final boolean wrappedArtist) {
        LinkedHashMap<Album, Integer> albums = new LinkedHashMap<>();

        for (Map.Entry<Song, Integer> song : listenedSongs.entrySet()) {
            Album album = null;
            if (wrappedArtist) {
                album = findAlbum(song.getKey());
            } else {
                album = findAlbumByName(song.getKey());
            }

            if (album != null) {
                if (albums.containsKey(album)) {
                    int count = albums.get(album) + song.getValue();
                    albums.replace(album, count);
                } else {
                    albums.put(album, song.getValue());
                }
            }
        }

        return albums;
    }

    public void addPodcast(Podcast podcast) {
        if (listenedPodcasts.containsKey(podcast)) {
            int count = listenedPodcasts.get(podcast);
            listenedPodcasts.put(podcast, count + 1);
        } else {
            listenedPodcasts.put(podcast, 1);
        }
    }

    public void addEpisode(Episode episode) {
        if (listenedEpisodes.containsKey(episode)) {
            int count = listenedEpisodes.get(episode);
            listenedEpisodes.put(episode, count + 1);
        } else {
            listenedEpisodes.put(episode, 1);
        }
    }

    public void addGenres(String genre, int nrSongs) {
        if (genres.containsKey(genre)) {
            int count = genres.get(genre) + nrSongs;
            genres.put(genre, count);
        } else {
            genres.put(genre, nrSongs);
        }
    }

    public void getGenres() {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            String genre = entry.getKey().getGenre();
            addGenres(genre, entry.getValue());
        }
    }

    public LinkedHashMap<String, Integer> addArtists(String artist, int nrSongs, LinkedHashMap<String, Integer> artists) {
        if (artists.containsKey(artist)) {
            int count = artists.get(artist) + nrSongs;
            artists.replace(artist, count);
        } else {
            artists.put(artist, nrSongs);
        }
        return artists;
    }

    public LinkedHashMap<String, Integer> getArtists(LinkedHashMap<String, Integer> artists) {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            String artist = entry.getKey().getArtist();
            artists = addArtists(artist, entry.getValue(), artists);
        }
        return artists;
    }

    public List<Map.Entry<String, Integer>> getTopSongs(final Integer timestamp) {
        player.calculateStatus(timestamp);
        LinkedHashMap<String, Integer> mergedSongs = new LinkedHashMap<>();

        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            if (mergedSongs.containsKey(entry.getKey().getName())) {
                int count = mergedSongs.get(entry.getKey().getName()) + entry.getValue();
                mergedSongs.replace(entry.getKey().getName(), count);
            } else {
                mergedSongs.put(entry.getKey().getName(), entry.getValue());
            }
        }
        return getTopEntriesSong(mergedSongs);
    }

    public List<Map.Entry<Album, Integer>> getTopAlbums(final Integer timestamp, final boolean wrappedArtist) {
        player.calculateStatus(timestamp);
        return getTopEntries(calculateAlbumStatus(wrappedArtist));
    }

    public List<Map.Entry<Episode, Integer>> getTopEpisodes() {
        return getTopEntries(listenedEpisodes);
    }

    public List<Map.Entry<String, Integer>> getTopGenres(final Integer timestamp) {
        player.calculateStatus(timestamp);
        getGenres();
        return getTopEntriesStrings(genres);
    }

    public List<Map.Entry<String, Integer>> getTopArtists(final Integer timestamp) {
        player.calculateStatus(timestamp);
        LinkedHashMap<String, Integer> artists = new LinkedHashMap<>();
        artists = getArtists(artists);
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

    private static List<Map.Entry<String, Integer>> getTopEntriesSong(LinkedHashMap<String, Integer> map) {
        return map.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    // Sort by count in descending order
                    int countComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (countComparison != 0) {
                        return countComparison;
                    }
                    // If counts are equal, sort lexicographically by key
                    return entry1.getKey().compareTo(entry2.getKey());
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    public LinkedHashMap<Album, Integer> getListenedAlbums(final boolean wrappedArtist) {
        return calculateAlbumStatus(wrappedArtist);
    }

    public LinkedHashMap<String, Integer> getListenedArtists() {
        LinkedHashMap<String, Integer> artists = new LinkedHashMap<>();
        artists = getArtists(artists);
        return artists;
    }

    public void deleteListenedSongPremium() {
        listenedSongsPremium.clear();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setAlbum(Album album) {
        listenedAlbums.replace(album, 0);
    }

    public LinkedHashMap<Song, Integer> getListenedSongs() {
        return listenedSongs;
    }

    public LinkedHashMap<Episode, Integer> getListenedEpisodes() {
        return listenedEpisodes;
    }

    public LinkedHashMap<Song, Integer> getListenedSongsPremium() {
        return listenedSongsPremium;
    }
}
