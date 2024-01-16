package app.users.user;

import app.audioFiles.AudioFiles;
import app.audioFiles.Song;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.podcasts.Episode;
import app.audioFiles.podcasts.Podcast;
import app.users.userComponents.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsersHistory {
    private LinkedHashMap<Song, Integer> listenedSongs;
    private LinkedHashMap<Album, Integer> listenedAlbums;
    private LinkedHashMap<Podcast, Integer> listenedPodcasts;
    private LinkedHashMap<Episode, Integer> listenedEpisodes;
    private LinkedHashMap<String, Integer> genres;
    private ArrayList<Album> allAlbums;
    private Player player;
    private final static int MAXSIZE = 5;
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

    /**
     * Adds a song to the list of listened premium songs.
     * If the song already exists in the list, increments its count.
     *
     * @param song The song to be added or incremented.
     */
    public void addSongPremium(final Song song) {
        if (listenedSongsPremium.containsKey(song)) {
            // If the song is already in the map, increment the count
            int count = listenedSongsPremium.get(song);
            listenedSongsPremium.replace(song, count + 1);
        } else {
            listenedSongsPremium.put(song, 1);
        }
    }

    /**
     * Calculates the total number of premium songs listened to.
     *
     * @return int The total count of listened premium songs.
     */
    public int totalPremiumSongs() {
        int count = 0;
        for (Map.Entry<Song, Integer> entry : listenedSongsPremium.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    /**
     * Adds a song to the list of listened songs.
     * If the song already exists in the list, increments its count.
     *
     * @param song The song to be added or incremented.
     */
    public void addSong(final Song song) {
        if (listenedSongs.containsKey(song)) {
            // If the song is already in the map, increment the count
            int count = listenedSongs.get(song);
            listenedSongs.replace(song, count + 1);
        } else {
            listenedSongs.put(song, 1);
        }
    }

    /**
     * Adds an album to the list of listened albums.
     * If the album already exists in the list, increments its count.
     *
     * @param album The album to be added or incremented.
     */
    public void addAlbum(final Album album) {
        if (listenedAlbums.containsKey(album)) {
            int count = listenedAlbums.get(album);
            listenedAlbums.replace(album, count + 1);
        } else {
            listenedAlbums.put(album, 1);
        }
    }

    /**
     * Finds an album based on a given song, matching both the album name and artist.
     * Used to accurately identify the album of a song in a collection where multiple
     * albums might have the same name but different artists.
     *
     * @param song The song for which the album is to be found.
     * @return Album The album matching the song's album name and artist, or null if not found.
     */
    private Album findAlbum(final Song song) {
        for (Album album : allAlbums) {
            if (album.getName().equals(song.getAlbum())
                    && album.getArtist().equals(song.getArtist())) {
                return album;
            }
        }
        return null;
    }

    /**
     * Finds an album by its name in the provided list of songs.
     *
     * @param song The song containing the album's name for which to search.
     * @return Album The album with the matching name, or null if not found.
     */
    private Album findAlbumByName(final Song song) {
        for (Album album : allAlbums) {
            if (album.getName().equals(song.getAlbum())) {
                return album;
            }
        }
        return null;
    }

    /**
     * Calculates and returns the status of albums based on listened songs.
     *
     * @param wrappedArtist A boolean to indicate whether to consider the artist when
     *                      calculating album status.
     * @return LinkedHashMap<Album, Integer> The album listening status with counts.
     */
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

    /**
     * Adds a podcast to the list of listened podcasts.
     * Increments the count if the podcast is already in the list.
     *
     * @param podcast The podcast to be added or incremented.
     */
    public void addPodcast(final Podcast podcast) {
        if (listenedPodcasts.containsKey(podcast)) {
            int count = listenedPodcasts.get(podcast);
            listenedPodcasts.put(podcast, count + 1);
        } else {
            listenedPodcasts.put(podcast, 1);
        }
    }

    /**
     * Adds an episode to the list of listened episodes.
     * Increments the count if the episode is already in the list.
     *
     * @param episode The episode to be added or incremented.
     */
    public void addEpisode(final Episode episode) {
        if (listenedEpisodes.containsKey(episode)) {
            int count = listenedEpisodes.get(episode);
            listenedEpisodes.put(episode, count + 1);
        } else {
            listenedEpisodes.put(episode, 1);
        }
    }

    /**
     * Adds a genre and the number of songs listened to in that genre.
     * Increments the count if the genre is already in the list.
     *
     * @param genre   The genre to be added or incremented.
     * @param nrSongs The number of songs listened to in that genre.
     */
    public void addGenres(final String genre, final int nrSongs) {
        if (genres.containsKey(genre)) {
            int count = genres.get(genre) + nrSongs;
            genres.put(genre, count);
        } else {
            genres.put(genre, nrSongs);
        }
    }

    /**
     * Compiles and updates the genres of all listened songs.
     */
    public void getGenres() {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            String genre = entry.getKey().getGenre();
            addGenres(genre, entry.getValue());
        }
    }

    /**
     * Adds an artist and the number of songs listened to for that artist.
     * Increments the count if the artist is already in the list.
     *
     * @param artist  The artist to be added or incremented.
     * @param nrSongs The number of songs listened to by that artist.
     * @param artists The map of artists to be updated.
     * @return LinkedHashMap<String, Integer> The updated map of artists with song counts.
     */
    public LinkedHashMap<String, Integer> addArtists(final String artist,
                                                     final int nrSongs,
                                                     final LinkedHashMap<String, Integer> artists) {
        if (artists.containsKey(artist)) {
            int count = artists.get(artist) + nrSongs;
            artists.replace(artist, count);
        } else {
            artists.put(artist, nrSongs);
        }
        return artists;
    }

    /**
     * Compiles a list of artists and their respective song listen counts from the listened songs.
     *
     * @param artists The initial map of artists to be updated.
     * @return LinkedHashMap<String, Integer> Updated map of artists with
     * the number of songs listened to.
     */
    public LinkedHashMap<String, Integer> getArtists(LinkedHashMap<String, Integer> artists) {
        for (Map.Entry<Song, Integer> entry : listenedSongs.entrySet()) {
            String artist = entry.getKey().getArtist();
            artists = addArtists(artist, entry.getValue(), artists);
        }
        return artists;
    }


    /**
     * Retrieves a list of the top songs based on the provided timestamp.
     * This method calculates the most listened songs within a specified time frame.
     *
     * @param timestamp The timestamp to calculate the song status.
     * @return List<Map.Entry < String, Integer>> List of top songs and their listen counts.
     */
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

    /**
     * Retrieves a list of the top albums based on the provided timestamp
     * and the 'wrappedArtist' flag.
     * This method calculates the most listened albums within a specified time frame.
     *
     * @param timestamp     The timestamp to calculate the album status.
     * @param wrappedArtist A flag indicating whether to consider the artist in
     *                      album identification.
     * @return List<Map.Entry < Album, Integer>> List of top albums and their listen counts.
     */
    public List<Map.Entry<Album, Integer>> getTopAlbums(final Integer timestamp,
                                                        final boolean wrappedArtist) {
        player.calculateStatus(timestamp);
        return getTopEntries(calculateAlbumStatus(wrappedArtist));
    }

    /**
     * Retrieves a list of the top episodes listened to.
     * This method sorts and limits the episodes based on their listening count.
     *
     * @return List<Map.Entry < Episode, Integer>> List of top episodes and their listen counts.
     */
    public List<Map.Entry<Episode, Integer>> getTopEpisodes() {
        return getTopEntries(listenedEpisodes);
    }

    /**
     * Retrieves a list of the top genres based on the provided timestamp.
     * This method calculates the most listened genres within a specified time frame.
     *
     * @param timestamp The timestamp to calculate the genre status.
     * @return List<Map.Entry < String, Integer>> List of top genres and their listen counts.
     */
    public List<Map.Entry<String, Integer>> getTopGenres(final Integer timestamp) {
        player.calculateStatus(timestamp);
        getGenres();
        return getTopEntriesStrings(genres);
    }

    /**
     * Retrieves a list of the top artists based on the provided timestamp.
     * This method calculates the most listened artists within a specified time frame.
     *
     * @param timestamp The timestamp to calculate the artist status.
     * @return List<Map.Entry < String, Integer>> List of top artists and their listen counts.
     */
    public List<Map.Entry<String, Integer>> getTopArtists(final Integer timestamp) {
        player.calculateStatus(timestamp);
        LinkedHashMap<String, Integer> artists = new LinkedHashMap<>();
        artists = getArtists(artists);
        return getTopEntriesStrings(artists);
    }

    /**
     * Retrieves a list of the top entries in a map sorted by their integer values
     * in descending order.
     * In case of a tie, the entries are sorted lexicographically by their keys.
     *
     * @param <T> The type of the keys in the map.
     * @param map The LinkedHashMap containing the data to be sorted.
     * @return List<Map.Entry < T, Integer>> A list of sorted map entries.
     */
    private static <T> List<Map.Entry<T, Integer>>
    getTopEntriesStrings(final LinkedHashMap<T, Integer> map) {
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
                .limit(MAXSIZE)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of the top audio file entries in a map sorted by their integer values
     * in descending order.
     * In case of a tie, the entries are sorted lexicographically by the name of the audio files.
     *
     * @param <T> The type of the audio files in the map.
     * @param map The LinkedHashMap containing the data to be sorted.
     * @return List<Map.Entry < T, Integer>> A list of sorted map entries.
     */
    private static <T extends AudioFiles> List<Map.Entry<T, Integer>>
    getTopEntries(final LinkedHashMap<T, Integer> map) {
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
                .limit(MAXSIZE)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a list of the top song entries in a map sorted by their integer values
     * in descending order.
     * In case of a tie, the entries are sorted lexicographically by the song names.
     *
     * @param map The LinkedHashMap containing the data to be sorted.
     * @return List<Map.Entry < String, Integer>> A list of sorted map entries.
     */
    private static List<Map.Entry<String, Integer>>
    getTopEntriesSong(final LinkedHashMap<String, Integer> map) {
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
                .limit(MAXSIZE)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a map of listened albums with their respective listen counts,
     * optionally considering the artist.
     *
     * @param wrappedArtist A boolean flag indicating whether to consider the
     *                      artist in album identification.
     * @return LinkedHashMap<Album, Integer> Map of listened albums and their listen counts.
     */
    public LinkedHashMap<Album, Integer> getListenedAlbums(final boolean wrappedArtist) {
        return calculateAlbumStatus(wrappedArtist);
    }

    /**
     * Retrieves a map of listened artists with their respective listen counts.
     *
     * @return LinkedHashMap<String, Integer> Map of listened artists and their listen counts.
     */
    public LinkedHashMap<String, Integer> getListenedArtists() {
        LinkedHashMap<String, Integer> artists = new LinkedHashMap<>();
        artists = getArtists(artists);
        return artists;
    }

    /**
     * Clears the list of listened premium songs.
     */
    public void deleteListenedSongPremium() {
        listenedSongsPremium.clear();
    }

    /**
     * Sets the Player object for the user's history.
     *
     * @param player The Player object to be associated with this history.
     */
    public void setPlayer(final Player player) {
        this.player = player;
    }

    /**
     * Resets the listen count of a specific album in the user's history.
     *
     * @param album The Album whose listen count is to be reset.
     */
    public void setAlbum(final Album album) {
        listenedAlbums.replace(album, 0);
    }

    /**
     * Retrieves a map of listened songs with their respective listen counts.
     *
     * @return LinkedHashMap<Song, Integer> A map of songs and their listen counts.
     */
    public LinkedHashMap<Song, Integer> getListenedSongs() {
        return listenedSongs;
    }

    /**
     * Retrieves a map of listened episodes with their respective listen counts.
     *
     * @return LinkedHashMap<Episode, Integer> A map of episodes and their listen counts.
     */
    public LinkedHashMap<Episode, Integer> getListenedEpisodes() {
        return listenedEpisodes;
    }

    /**
     * Retrieves a map of listened premium songs with their respective listen counts.
     *
     * @return LinkedHashMap<Song, Integer> A map of premium songs and their listen counts.
     */
    public LinkedHashMap<Song, Integer> getListenedSongsPremium() {
        return listenedSongsPremium;
    }

}
