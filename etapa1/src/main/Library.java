package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public final class Library implements GeneralStatistics {
    private final ArrayList<Song> songs;
    private final ArrayList<Podcast> podcasts;
    private final LinkedHashMap<String, User> users;
    private final ArrayList<Playlist> allPlaylists;
    private static final int MAX = 5;

    /**
     * Constructor for Library class
     *
     * @param library LibraryInput object
     */
    public Library(final LibraryInput library) {
        allPlaylists = new ArrayList<>();

        // Populate the songs list
        songs = new ArrayList<>();
        for (SongInput song : library.getSongs()) {
            Song newSong = new Song(song.getName(),
                    song.getDuration(),
                    song.getAlbum(),
                    song.getTags(),
                    song.getLyrics(),
                    song.getGenre(),
                    song.getReleaseYear(),
                    song.getArtist());
            songs.add(newSong);
        }

        // Populate the podcasts list
        podcasts = new ArrayList<>();
        for (PodcastInput podcast : library.getPodcasts()) {
            ArrayList<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                episodes.add(new Episode(episode.getName(),
                        episode.getDuration(),
                        episode.getDescription()));
            }
            Podcast newPodcast = new Podcast(podcast.getName(), podcast.getOwner(), episodes);
            podcasts.add(newPodcast);
        }

        // Populate the users list
        users = new LinkedHashMap<>();
        for (UserInput user : library.getUsers()) {
            User newUser = new User(user.getUsername(), user.getAge(), user.getCity(),
                    songs, podcasts, allPlaylists);
            users.put(newUser.getUsername(), newUser);
        }
    }

    /**
     * This method is used get the top 5 songs based on the number of likes they have.
     *
     * @param command The command containing information about the show top 5 songs operation.
     * @return An ObjectNode representing the result of the show top 5 songs operation.
     */
    public ObjectNode getTop5Songs(final Command command) {
        return getTopResults(command,
                songs,
                Comparator.comparingInt(Song::getLikes).reversed(), Song::getName);
    }

    /**
     * This method is used get the top 5 playlists based on the number of followers they have.
     *
     * @param command The command containing information about the show top 5 playlists operation.
     * @return An ObjectNode representing the result of the show top 5 playlists operation.
     */
    public ObjectNode getTop5Playlists(final Command command) {
        return getTopResults(command,
                allPlaylists,
                Comparator.comparingInt(Playlist::getFollowers).reversed(),
                Playlist::getName);
    }

    /**
     * Generic method for obtaining the top 5 results for any given list and sorting criteria
     *
     * @param command       The received command
     * @param items         The list of objects
     * @param comparator    The comparator for sorting
     * @param nameExtractor The function to extract the name from objects
     * @param <T>           The type of objects in the list
     * @return An ObjectNode containing the results
     */
    private <T> ObjectNode getTopResults(final Command command,
                                         final List<T> items,
                                         final Comparator<T> comparator,
                                         final Function<T, ?> nameExtractor) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("timestamp", command.getTimestamp());

        // Clone the list and sort it
        List<T> sortedItems = new ArrayList<>(items);
        sortedItems.sort(comparator);

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (int i = 0; i < Math.min(MAX, sortedItems.size()); i++) {
            resultsArray.add(String.valueOf(nameExtractor.apply(sortedItems.get(i))));
        }
        resultNode.set("result", resultsArray);
        return resultNode;
    }

    /**
     * Method for finding a song in the library
     *
     * @param name of the song
     * @return the song if it exists, null otherwise
     */
    public Song findSong(final String name) {
        for (Song song : songs) {
            if (song.getName().equals(name)) {
                return song;
            }
        }
        return null;
    }

    /**
     * Method for finding a podcast in the library
     *
     * @param name of the podcast
     * @return the podcast if it exists, null otherwise
     */
    public Podcast findPodcast(final String name) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(name)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * Method for finding a playlist by name and visibility
     *
     * @param name of the playlist
     * @return the user if it exists, null otherwise
     */
    public Playlist findPlaylist(final String name) {
        for (Playlist playlist : allPlaylists) {
            if (playlist.getName().equals(name) && playlist.isVisibility()) {
                return playlist;
            }
        }
        return null;
    }

    /**
     * Retrieves the list of songs in the music library.
     *
     * @return An ArrayList of Song objects representing the songs in the music library.
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     * Retrieves the collection of users in the system.
     *
     * @return A LinkedHashMap where the keys are user IDs and the values are User objects.
     */
    public LinkedHashMap<String, User> getUsers() {
        return users;
    }

}
