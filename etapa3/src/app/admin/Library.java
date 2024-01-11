package app.admin;

import app.recommandations.FansPlaylistRecommendation;
import app.recommandations.RandomPlaylistRecommendation;
import app.recommandations.RandomSongRecommendation;
import app.recommandations.RecommendationContext;
import app.userPages.ArtistPage;
import app.userPages.HostPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.audioCollection.Playlist;
import app.audioFiles.Song;
import app.audioFiles.podcasts.Episode;
import app.audioFiles.podcasts.Podcast;
import app.users.arist.Artist;
import app.users.Host;
import app.users.user.User;
import app.users.userComponents.publicity.Announcement;
import app.users.userComponents.publicity.Event;
import app.users.userComponents.publicity.Merch;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Library implements GeneralStatistics {
    // Lazily instantiated Singleton instance
    private static Library instance = null;
    private final ArrayList<Song> songs;
    private final ArrayList<Podcast> podcasts;
    private final LinkedHashMap<String, User> users;
    private final ArrayList<String> deletedUsers;
    private final LinkedHashMap<String, Artist> artists;
    private final LinkedHashMap<String, Host> hosts;
    private final ArrayList<Playlist> allPlaylists;
    private static final int MAXSIZE = 5;
    private final ObjectMapper objectMapper;
    private ArrayList<Album> allAlbums;
    private ArrayList<Announcement> allAnnouncements;

    /**
     * Constructor for Library class
     *
     * @param library LibraryInput object
     */
    private Library(final LibraryInput library) {
        allPlaylists = new ArrayList<>();
        allAlbums = new ArrayList<>();
        allAnnouncements = new ArrayList<>();
        hosts = new LinkedHashMap<>();
        artists = new LinkedHashMap<>();
        deletedUsers = new ArrayList<>();

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
                        episode.getDescription(),
                        podcast.getOwner()));
            }
            Podcast newPodcast = new Podcast(podcast.getName(), podcast.getOwner(), episodes);
            podcasts.add(newPodcast);
        }

        // Populate the users list
        users = new LinkedHashMap<>();
        for (UserInput user : library.getUsers()) {
            User newUser = new User(user.getUsername(), user.getAge(), user.getCity(),
                    songs, podcasts, allPlaylists, allAlbums, this);
            users.put(newUser.getUsername(), newUser);
        }
        objectMapper = new ObjectMapper();
    }

    /**
     * Returns the singleton instance of the Library class, lazily initializing
     * it with the provided LibraryInput on the first invocation.
     *
     * @param library The initial LibraryInput to be used for the library initialization.
     * @return The singleton instance of the Library class.
     */
    public static Library getInstance(final LibraryInput library) {
        if (instance == null) {
            instance = new Library(library);
        }
        return instance;
    }

    /**
     * Makes the instance null after a test is done.
     */
    public void resetInstance() {
        instance = null;
    }

    private ObjectNode createResultNode(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        return resultNode;
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
     * This method is used to get the top 5 albums based on the number of likes.
     *
     * @param command The command containing information about the show top 5 albums operation.
     * @return An ObjectNode representing the result of the show top 5 albums operation.
     */
    public ObjectNode getTop5Albums(final Command command) {
        return getTopResults(command,
                allAlbums,
                Comparator
                        .comparingInt(Album::getTotalLikes)
                        .reversed()
                        .thenComparing(Album::getName, String.CASE_INSENSITIVE_ORDER),
                Album::getName);
    }

    /**
     * This method is used to get the top 5 artists based on the number of likes.
     *
     * @param command The command containing information about the show top 5 artists operation.
     * @return An ObjectNode representing the result of the show top 5 artists operation.
     */
    public ObjectNode getTop5Artists(final Command command) {
        return getTopResults(command,
                new ArrayList<>(artists.values()),
                Comparator.comparingInt(Artist::getAllLikes).reversed(),
                Artist::getName);
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
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("timestamp", command.getTimestamp());

        // Clone the list and sort it
        List<T> sortedItems = new ArrayList<>(items);
        sortedItems.sort(comparator);

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (int i = 0; i < Math.min(MAXSIZE, sortedItems.size()); i++) {
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

    public Artist findArtist(final String name) {
        for (Artist artist : artists.values()) {
            if (artist.getName().equals(name)) {
                return artist;
            }
        }
        return null;
    }

    public Host findHost(final String name) {
        for (Host host : hosts.values()) {
            if (host.getName().equals(name)) {
                return host;
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
     * Method for finding an album by name
     *
     * @param name of the album
     * @return the album if it exists, null otherwise
     */
    public Album findAlbum(final String name) {
        for (Album album : allAlbums) {
            if (album.getName().equals(name)) {
                return album;
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
     * Method for adding a user, artist or host to the library
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode addUser(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (users.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " is already taken.");
            return resultNode;
        }

        if (hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " is already taken.");
            return resultNode;
        }

        if (artists.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " is already taken.");
            return resultNode;
        }

        switch (command.getType()) {
            case "user":
                User newUser = new User(command.getUsername(), command.getAge(), command.getCity(),
                        songs, podcasts, allPlaylists, allAlbums, this);
                users.put(command.getUsername(), newUser);
                break;
            case "artist":
                Artist newArtist =
                        new Artist(allAlbums, command.getUsername());
                artists.put(command.getUsername(), newArtist);
                break;
            case "host":
                Host newHost = new Host(podcasts, allAnnouncements, command.getUsername());
                hosts.put(command.getUsername(), newHost);
                break;
            default:
                break;
        }

        resultNode.put("message", "The username " + command.getUsername()
                + " has been added successfully.");

        return resultNode;
    }

    /**
     * Method for finding all the online users
     *
     * @param command that is given
     * @return ObjectNode containing the result, the online users
     */
    public ObjectNode getOnlineUsers(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("timestamp", command.getTimestamp());

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            if (entry.getValue().getUserStatus().equals("online")) {
                resultsArray.add(entry.getKey());
            }
        }

        resultNode.set("result", resultsArray);
        return resultNode;
    }

    private boolean noSongsPlaying(final Artist artist) {
        for (Map.Entry<String, Album> entry : artist.getAlbums().entrySet()) {
            for (Song song : entry.getValue().getSongs()) {
                if (song.getListeners() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean noAlbumsPlaying(final Artist artist) {
        for (Map.Entry<String, Album> entry : artist.getAlbums().entrySet()) {
            if (entry.getValue().getListeners() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean noPlaylistsPlaying(final User user) {
        for (Playlist playlist : user.getPlaylists()) {
            if (playlist.getListeners() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean noPodcastsPlaying(final Host host) {
        for (Podcast podcast : host.getPodcasts()) {
            if (podcast.getListeners() > 0) {
                return false;
            }
        }
        return true;
    }

    private void deleteArtist(final Command command, final ObjectNode resultNode) {
        Artist artist = artists.get(command.getUsername());

        for (Map.Entry<String, User> entry : users.entrySet()) {
            if (!entry.getValue().getPlayer().getPlayMode().equals("clear")) {
                entry.getValue().getPlayer().calculateStatus(command.getTimestamp());
            }
        }

        // delete the artists only if no one is on their page and no one is
        // listening to their songs
        if (artist.getArtistPage().getListeners() == 0 && noSongsPlaying(artist)
                && noAlbumsPlaying(artist)) {
            for (Map.Entry<String, Album> entry : artist.getAlbums().entrySet()) {
                songs.removeAll(entry.getValue().getSongs());
                allAlbums.remove(entry.getValue());
            }

            // if any usere had liked songs from the artist, they are removed
            for (Map.Entry<String, User> entry : users.entrySet()) {
                for (Map.Entry<String, Album> entryAlbum : artist.getAlbums().entrySet()) {
                    entry.getValue().getPlayer().getLikedSongs()
                            .removeAll(entryAlbum.getValue().getSongs());
                }
            }

            artists.remove(command.getUsername());
            resultNode.put("message", command.getUsername()
                    + " was successfully deleted.");
            deletedUsers.add(command.getUsername());
        } else {
            resultNode.put("message", command.getUsername()
                    + " can't be deleted.");
        }
    }

    private void deleteHost(final Command command, final ObjectNode resultNode) {
        Host host = hosts.get(command.getUsername());

        for (Map.Entry<String, User> entry : users.entrySet()) {
            if (!entry.getValue().getPlayer().getPlayMode().equals("clear")) {
                entry.getValue().getPlayer().calculateStatus(command.getTimestamp());
            }
        }

        if (host.getHostPage() != null) {
            // delete the hosts only if no one is on their page and no one is
            // listening to their podcasts
            if (host.getHostPage().getListeners() == 0 && noPodcastsPlaying(host)) {
                for (Podcast podcast : host.getHostPage().getPodcasts()) {
                    podcasts.remove(podcast);
                }

                hosts.remove(command.getUsername());
                resultNode.put("message", command.getUsername()
                        + " was successfully deleted.");
                deletedUsers.add(command.getUsername());
            } else {
                resultNode.put("message", command.getUsername()
                        + " can't be deleted.");
            }
        }
    }

    private void deleteNormalUser(final Command command, final ObjectNode resultNode) {
        User user = users.get(command.getUsername());

        for (Map.Entry<String, User> entry : users.entrySet()) {
            if (!entry.getValue().getPlayer().getPlayMode().equals("clear")) {
                entry.getValue().getPlayer().calculateStatus(command.getTimestamp());
            }
        }

        // delete the users only if no one is listening to their playlists
        if (noPlaylistsPlaying(user)) {
            for (Playlist playlist : user.getPlaylists()) {
                songs.removeAll(playlist.getSongs());
                allPlaylists.remove(playlist);
            }

            // delete the songs from the liked songs of the users
            for (Map.Entry<String, User> entry : users.entrySet()) {
                for (Playlist playlist : user.getPlaylists()) {
                    entry.getValue().getPlayer().getLikedSongs().removeAll(playlist.getSongs());
                }
            }

            // delete the playlists from the following playlists of the users
            for (Map.Entry<String, User> entry : users.entrySet()) {
                for (Playlist playlist : user.getPlaylists()) {
                    entry.getValue().getFollowingPlaylists().remove(playlist);
                }
            }

            // if the user liked or followed another song/playlist, unlike them
            for (Song song : user.getPlayer().getLikedSongs()) {
                song.unlikeSong();
            }

            for (Playlist playlist : user.getFollowingPlaylists()) {
                playlist.unfollow();
            }

            users.remove(command.getUsername());
            resultNode.put("message", command.getUsername()
                    + " was successfully deleted.");
            deletedUsers.add(command.getUsername());
        } else {
            resultNode.put("message", command.getUsername()
                    + " can't be deleted.");
        }
    }

    /**
     * Method for deleting a user, artist or host from the library
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode deleteUser(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (users.containsKey(command.getUsername())) {
            User user = users.get(command.getUsername());
            if (user.isPremium()) {
                resultNode.put("message", command.getUsername()
                        + " can't be deleted.");
                return resultNode;
            }
        }


        if (deletedUsers.contains(command.getUsername())) {
            resultNode.put("message", command.getUsername()
                    + " can't be deleted.");
            return resultNode;
        }

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }
        if (artists.containsKey(command.getUsername())) {
            deleteArtist(command, resultNode);
        } else if (hosts.containsKey(command.getUsername())) {
            deleteHost(command, resultNode);
        } else {
            deleteNormalUser(command, resultNode);
        }

        return resultNode;
    }

    /**
     * Method for displaying the users, artists and hosts from the library
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode getAllUsers(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("timestamp", command.getTimestamp());

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            resultsArray.add(entry.getKey());
        }

        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            resultsArray.add(entry.getKey());
        }

        for (Map.Entry<String, Host> entry : hosts.entrySet()) {
            resultsArray.add(entry.getKey());
        }

        resultNode.set("result", resultsArray);
        return resultNode;
    }

    /**
     * Method for adding an album for an artist
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode addAlbum(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (artists.containsKey(command.getUsername())) {
            Artist artist = artists.get(command.getUsername());
            Album newAlbum = new Album(command.getName(), command.getUsername(),
                    command.getReleaseYear(), command.getDescription(), command.getSongs());
            int result = artist.addAlbum(newAlbum);

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " has another album with the same name.");
                return resultNode;
            } else if (result == 1) {
                resultNode.put("message", command.getUsername()
                        + " has the same song at least twice in this album.");
                return resultNode;
            } else {
                songs.addAll(newAlbum.getSongs());
                resultNode.put("message", command.getUsername()
                        + " has added new album successfully.");

//                notifyUsers(command.getUsername(), "New Album",
//                        "New Album from " + command.getUsername() + ".");
                artist.notifyObservers("New Album",
                        "New Album from " + command.getUsername() + ".");

                return resultNode;
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not an artist.");
            return resultNode;
        }
    }

//    private void notifyUsers(final String username, final String type, final String description) {
//        for (Map.Entry<String, User> entry : users.entrySet()) {
//            if (entry.getValue().getSubscribeArtists().contains(username)) {
//                entry.getValue().getNotifications().addNotification(type, description);
//            }
//        }
//    }

    /**
     * Method for removing an album form an artist
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode removeAlbum(final Command command, final Library library) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (artists.containsKey(command.getUsername())) {
            Artist artist = artists.get(command.getUsername());
            int result = artist.removeAlbum(command.getName(), library);

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " doesn't have an album with the given name.");
                return resultNode;
            } else if (result == 1) {
                resultNode.put("message", command.getUsername()
                        + " can't delete this album.");
                return resultNode;
            } else {
                resultNode.put("message", command.getUsername()
                        + " deleted the album successfully.");
                return resultNode;
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not an artist.");
            return resultNode;
        }
    }

    /**
     * Method for displaying the albums of a given artist
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode showAlbums(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        Artist artist = artists.get(command.getUsername());

        ArrayNode resultsArray = objectMapper.createArrayNode();

        for (Map.Entry<String, Album> album : artist.getAlbums().entrySet()) {
            ObjectNode newNode = objectMapper.createObjectNode();
            ArrayNode resultsArraySongs = objectMapper.createArrayNode();

            newNode.put("name", album.getKey());

            for (Song song : album.getValue().getSongs()) {
                resultsArraySongs.add(song.getName());
            }

            newNode.set("songs", resultsArraySongs);

            resultsArray.add(newNode);
        }

        resultNode.set("result", resultsArray);
        return resultNode;
    }

    /**
     * Method for creating an event for an artist
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode addEvent(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }
        if (artists.containsKey(command.getUsername())) {
            Artist artist = artists.get(command.getUsername());
            Event newEvent = new Event(command.getUsername(), command.getName(),
                    command.getDescription(), command.getDate());

            int result = artist.addEvent(newEvent, resultNode);
            if (result == 0) {
                resultNode.put("message", "Event for " + newEvent.getOwner()
                        + " does not have a valid date.");
            } else if (result == 1) {
                resultNode.put("message", newEvent.getOwner()
                        + " has another event with the same name.");
            } else {
                resultNode.put("message", newEvent.getOwner()
                        + " has added new event successfully.");

//                notifyUsers(command.getUsername(), "New Event",
//                        "New Event from " + command.getUsername() + ".");
                artist.notifyObservers("New Event",
                        "New Event from " + command.getUsername() + ".");
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not an artist.");
            return resultNode;
        }
        return resultNode;
    }

    /**
     * Method for removing an event from an artist
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode removeEvent(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (artists.containsKey(command.getUsername())) {
            Artist artist = artists.get(command.getUsername());
            int result = artist.removeEvent(command.getName());

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " doesn't have an event with the given name.");
            } else {
                resultNode.put("message", command.getUsername()
                        + " deleted the event successfully.");
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not an artist.");
        }
        return resultNode;
    }

    /**
     * Method for adding a merch for an artist
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode addMerch(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (artists.containsKey(command.getUsername())) {
            Artist artist = artists.get(command.getUsername());
            Merch newMerch = new Merch(command.getUsername(), command.getName(),
                    command.getDescription(), command.getPrice());
            int result = artist.addMerch(newMerch, resultNode);

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " has merchandise with the same name.");
            } else if (result == 1) {
                resultNode.put("message", "Price for merchandise can not be negative.");
            } else {
                resultNode.put("message", command.getUsername()
                        + " has added new merchandise successfully.");

//                notifyUsers(command.getUsername(), "New Merchandise",
//                        "New Merchandise from " + command.getUsername() + ".");
                artist.notifyObservers("New Merchandise",
                        "New Merchandise from " + command.getUsername() + ".");
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not an artist.");
        }
        return resultNode;
    }

//    private void notifyUsersHosts(final String username, final String type, final String description) {
//        for (Map.Entry<String, User> entry : users.entrySet()) {
//            if (entry.getValue().getSubscribeHosts().contains(username)) {
//                entry.getValue().getNotifications().addNotification(type, description);
//            }
//        }
//    }

    /**
     * Method for adding a podcast for a host
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode addPodcast(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (hosts.containsKey(command.getUsername())) {
            Host host = hosts.get(command.getUsername());
            Podcast newPodcast = new Podcast(command.getName(), command.getUsername(),
                    command.getEpisodes());
            int result = host.addPodcast(newPodcast);

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " has another podcast with the same name.");
                return resultNode;
            } else if (result == 1) {
                resultNode.put("message", command.getUsername()
                        + " has the same episode in this podcast.");
                return resultNode;
            } else {
                resultNode.put("message", command.getUsername()
                        + " has added new podcast successfully.");

//                notifyUsersHosts(command.getUsername(), "New Podcast",
//                        "New Podcast from " + command.getUsername() + ".");
                host.notifyObservers("New Podcast",
                        "New Podcast from " + command.getUsername() + ".");

                return resultNode;
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not a host.");
            return resultNode;
        }
    }

    /**
     * Method for removing a podcast from a host
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode removePodcast(final Command command, final Library library) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (hosts.containsKey(command.getUsername())) {
            Host host = hosts.get(command.getUsername());
            int result = 0;

            for (Podcast podcast : podcasts) {
                if (podcast.getName().equals(command.getName())) {
                    result = host.removePodcast(podcast);
                    break;
                }
            }

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " doesn't have a podcast with the given name.");
                return resultNode;
            } else if (result == 1) {
                resultNode.put("message", command.getUsername()
                        + " can't delete this podcast.");
                return resultNode;
            } else {
                resultNode.put("message", command.getUsername()
                        + " deleted the podcast successfully.");
                return resultNode;
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not a host.");
            return resultNode;
        }
    }

    /**
     * Method for displaying the podcasts of a given host
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode showPodcasts(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        Host host = hosts.get(command.getUsername());

        ArrayNode resultsArray = objectMapper.createArrayNode();

        for (Podcast podcast : host.getPodcasts()) {
            ObjectNode newNode = objectMapper.createObjectNode();
            ArrayNode resultsArraySongs = objectMapper.createArrayNode();

            newNode.put("name", podcast.getName());

            for (Episode episode : podcast.getEpisodes()) {
                resultsArraySongs.add(episode.getName());
            }

            newNode.set("episodes", resultsArraySongs);

            resultsArray.add(newNode);
        }

        resultNode.set("result", resultsArray);
        return resultNode;
    }

    /**
     * Method for adding an announcement for a host
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode addAnnouncement(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (hosts.containsKey(command.getUsername())) {
            Host host = hosts.get(command.getUsername());
            Announcement newAnnouncement = new Announcement(command.getName(),
                    command.getDescription(), command.getUsername());
            int result = host.addAnnouncement(newAnnouncement);

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " has already added an announcement with this name.");
            } else {
                resultNode.put("message", command.getUsername()
                        + " has successfully added new announcement.");

//                notifyUsersHosts(command.getUsername(), "New Announcement",
//                        "New Announcement from " + command.getUsername() + ".");
                host.notifyObservers("New Announcement",
                        "New Announcement from " + command.getUsername() + ".");
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not a host.");
        }
        return resultNode;
    }

    /**
     * Method for removing an announcement from a host
     *
     * @param command that is given
     * @return ObjectNode containing the result, the message
     */
    public ObjectNode removeAnnouncement(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        if (hosts.containsKey(command.getUsername())) {
            Host host = hosts.get(command.getUsername());
            Announcement removedAnnouncement = new Announcement(command.getName(),
                    command.getDescription(), command.getUsername());
            int result = host.removeAnnouncement(removedAnnouncement.getName());

            if (result == 0) {
                resultNode.put("message", command.getUsername()
                        + " has successfully deleted the announcement.");
            } else {
                resultNode.put("message", command.getUsername()
                        + " has no announcement with the given name.");
            }
        } else {
            resultNode.put("message", command.getUsername() + " is not a host.");
        }
        return resultNode;
    }

    public ObjectNode subscribe(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());

        if (!user.getCurrentPageType().equals("artist") && !user.getCurrentPageType().equals("host")) {
            resultNode.put("message", "To subscribe you need to be on the page of an artist or host.");
            return resultNode;
        }

        int ret;

        if (user.getCurrentPageType().equals("artist")) {
            ArtistPage page = (ArtistPage) user.getCurrentPage();

            ret = user.subscribeArtist(page.getName());
            Artist artist = artists.get(page.getName());

            if (ret == 0) {
                resultNode.put("message", command.getUsername() + " unsubscribed from "
                        + page.getName() + " successfully.");
                artist.removeObserver(user);
            } else {
                resultNode.put("message", command.getUsername() + " subscribed to "
                        + page.getName() + " successfully.");
                artist.registerObserver(user);
            }
        } else {
            HostPage hostPage = (HostPage) user.getCurrentPage();

            ret = user.subscribeHost(hostPage.getName());

            Host host = hosts.get(hostPage.getName());

            if (ret == 0) {
                resultNode.put("message", command.getUsername() + " unsubscribed from "
                        + hostPage.getName() + " successfully.");
                host.removeObserver(user);
            } else {
                resultNode.put("message", command.getUsername() + " subscribed to "
                        + hostPage.getName() + " successfully.");
                host.registerObserver(user);
            }
        }

        return resultNode;
    }

    public ObjectNode getNotifications(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!users.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());

        resultNode = user.getNotifications().getNotifications(resultNode);

        return resultNode;
    }

    public ObjectNode buyMerch(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!users.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());

        if (!user.getCurrentPageType().equals("artist")) {
            resultNode.put("message", "Cannot buy merch from this page.");
            return resultNode;
        }

        double price = -1;
        ArtistPage page = (ArtistPage) user.getCurrentPage();
        for (Merch merch : page.getMerch()) {
            if (merch.getName().equals(command.getName())) {
                price = merch.getPrice();
                break;
            }
        }

        if (price == -1) {
            resultNode.put("message", "The merch " + command.getName() + " doesn't exist.");
            return resultNode;
        }

        user.buyMerch(command.getName());
        page.increaseMerchRevenue(price);
        resultNode.put("message", command.getUsername() + " has added new merch successfully.");

        return resultNode;
    }

    public ObjectNode seeMerch(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!users.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());

        ArrayNode resultsArray = objectMapper.createArrayNode();

        for (String merch : user.getBoughtMerch()) {
            resultsArray.add(merch);
        }

        resultNode.set("result", resultsArray);

        return resultNode;
    }

    public String getMostProfitableSong(final Artist artist) {
        double max = 0;
        String mostProfitableSong = "N/A";

//        for (Map.Entry<String, Album> entry : artist.getAlbums().entrySet()) {
//            for (Song song : entry.getValue().getSongs()) {
//                if (song.getRevenue() > max) {
//                    max = song.getRevenue();
//                    mostProfitableSong = song.getName();
//                }
//            }
//        }
        List<Song> allSongs = new ArrayList<>();

// Populate the list with songs from all albums
        for (Map.Entry<String, Album> entry : artist.getAlbums().entrySet()) {
            allSongs.addAll(entry.getValue().getSongs());
        }

// Sort the songs lexicographically by their name
        Collections.sort(allSongs, Comparator.comparing(Song::getName));

// Iterate through the sorted list to find the most profitable song
        for (Song song : allSongs) {
            if (song.getRevenue() > max) {
                max = song.getRevenue();
                mostProfitableSong = song.getName();
            }
        }
        return mostProfitableSong;
    }

    public ObjectNode endProgram() {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "endProgram");

        ObjectNode resultObjectNode = objectMapper.createObjectNode();

        for (Map.Entry<String, User> entry : users.entrySet()) {
            if (entry.getValue().isPremium()) {
                entry.getValue().cancelPremium();
                calculateRevenue(entry.getValue());
            }
        }

        // Custom comparator for sorting based on song revenue, then merch revenue, and then lexicographically
        Comparator<Artist> revenueComparator = Comparator
                .comparing((Artist artist) -> artist.getSongRevenue() == 0 ? 0 : 1, Comparator.reverseOrder())
                .thenComparing((Artist artist) -> artist.getSongRevenue(), Comparator.reverseOrder())
                .thenComparing(artist -> artist.getArtistPage().getMerchRevenue(), Comparator.reverseOrder())
                .thenComparing(Artist::getName);

        List<Artist> sortedArtists = artists.values().stream()
                .sorted(revenueComparator)
                .collect(Collectors.toList());

        int rank = 1;
        String mostProfitableSong;
        double songRevenue;

        for (Artist entry : sortedArtists) {
            ObjectNode nodeArray = objectMapper.createObjectNode();

            if (entry.getArtistPage().getMerchRevenue() > 0 || entry.getHasBeenListenedTo()) {

                nodeArray.put("merchRevenue", entry.getArtistPage().getMerchRevenue());

                songRevenue = Math.round(entry.getSongRevenue() * 100.0) / 100.0;
                nodeArray.put("songRevenue", songRevenue);

                nodeArray.put("ranking", rank);
                rank++;

                mostProfitableSong = getMostProfitableSong(entry);
                nodeArray.put("mostProfitableSong", mostProfitableSong);

                resultObjectNode.set(entry.getName(), nodeArray);
            }
        }
        resultNode.set("result", resultObjectNode);

        return resultNode;
    }

    public ObjectNode handleRecommendation(Command command) {
        RecommendationContext context = new RecommendationContext();

        if (command.getRecommendationType().equals("random_song")) {
            context.setStrategy(new RandomSongRecommendation());
        } else if (command.getRecommendationType().equals("fans_playlist")) {
            context.setStrategy(new FansPlaylistRecommendation(users));
        } else {
            context.setStrategy(new RandomPlaylistRecommendation());
        }

        return context.executeStrategy(command, users, artists, hosts, songs);
    }

    public LinkedHashMap<User, Integer> getTop5Fans(final String artistName, final Integer timestamp) {
        LinkedHashMap<User, Integer> topFans = new LinkedHashMap<>();

        for (Map.Entry<String, User> entry : users.entrySet()) {
            entry.getValue().getPlayer().calculateStatus(timestamp);
            for (Map.Entry<String, Integer> artist : entry.getValue().getUsersHistory().getListenedArtists().entrySet()) {
                if (artist.getKey().equals(artistName)) {
                    topFans.put(entry.getValue(), artist.getValue());
                }
            }
        }

        LinkedHashMap<User, Integer> sortedTopFans = topFans.entrySet().stream()
                .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        return sortedTopFans;
    }


    public ObjectNode wrapped(final Command command) {
        ObjectNode resultNode;
        if (artists.containsKey(command.getUsername())) {
            resultNode = wrappedArtist(command);
        } else if (hosts.containsKey(command.getUsername())) {
            resultNode = wrappedHost(command);
        } else {
            resultNode = wrappedUser(command);
        }
        return resultNode;
    }

    public ObjectNode wrappedUser(final Command command) {
        ObjectNode resultNode = createResultNode(command);


        ObjectNode resultObjectNode = objectMapper.createObjectNode();

        if (artists.containsKey(command.getUsername())) {
            return resultObjectNode;
        } else if (hosts.containsKey(command.getUsername())) {
            return resultObjectNode;
        }

        if (!users.containsKey(command.getUsername())) {
            resultObjectNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultObjectNode;
        }

        User user = users.get(command.getUsername());
        user.getPlayer().calculateStatus(command.getTimestamp());


        ObjectNode resultNodeArtist = objectMapper.createObjectNode();

        if (user.getUsersHistory().getTopArtists(command.getTimestamp()).isEmpty() &&
                user.getUsersHistory().getTopGenres(command.getTimestamp()).isEmpty() &&
                user.getUsersHistory().getTopSongs(command.getTimestamp()).isEmpty() &&
                user.getUsersHistory().getTopAlbums(command.getTimestamp(), false).isEmpty() &&
                user.getUsersHistory().getTopEpisodes().isEmpty()) {

            resultNode.put("message", "No data to show for user " + command.getUsername() + ".");
            return resultNode;
        }

        List<Map.Entry<String, Integer>> topArtist = user.getUsersHistory().getTopArtists(command.getTimestamp());
        for (Map.Entry<String, Integer> entry : topArtist) {
            resultNodeArtist.put(entry.getKey(), entry.getValue());
        }

        resultObjectNode.set("topArtists", resultNodeArtist);

        ObjectNode topGenresArray = objectMapper.createObjectNode();

        List<Map.Entry<String, Integer>> topGenres = user.getUsersHistory().getTopGenres(command.getTimestamp());
        for (Map.Entry<String, Integer> entry : topGenres) {
            topGenresArray.put(entry.getKey(), entry.getValue());
        }
        resultObjectNode.set("topGenres", topGenresArray);


        ObjectNode topSongsArray = objectMapper.createObjectNode();

        List<Map.Entry<String, Integer>> topSongs = user.getUsersHistory().getTopSongs(command.getTimestamp());
        for (Map.Entry<String, Integer> entry : topSongs) {
            topSongsArray.put(entry.getKey(), entry.getValue());
        }
        resultObjectNode.set("topSongs", topSongsArray);

        ObjectNode topAlbumsArray = objectMapper.createObjectNode();

        List<Map.Entry<Album, Integer>> topAlbums = user.getUsersHistory().getTopAlbums(command.getTimestamp(), false);
        for (Map.Entry<Album, Integer> entry : topAlbums) {
            topAlbumsArray.put(entry.getKey().getName(), entry.getValue());
        }
        resultObjectNode.set("topAlbums", topAlbumsArray);


        ObjectNode topEpisodesArray = objectMapper.createObjectNode();

        List<Map.Entry<Episode, Integer>> topEpisodes = user.getUsersHistory().getTopEpisodes();
        for (Map.Entry<Episode, Integer> entry : topEpisodes) {
            topEpisodesArray.put(entry.getKey().getName(), entry.getValue());
        }
        resultObjectNode.set("topEpisodes", topEpisodesArray);

        resultNode.set("result", resultObjectNode);

        return resultNode;
    }

    public static List<Map.Entry<Episode, Integer>> getTop5Episodes(LinkedHashMap<Episode, Integer> hostsEpisodes) {
        return hostsEpisodes.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }

    public ObjectNode wrappedHost(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        Host host = hosts.get(command.getUsername());

        ObjectNode resultObjectNode = JsonNodeFactory.instance.objectNode();

        LinkedHashMap<Episode, Integer> hostsEpisodes = new LinkedHashMap<>();

        for (User user : host.getListenersList()) {
            user.getPlayer().calculateStatus(command.getTimestamp());
            LinkedHashMap<Episode, Integer> userEpisodes = new LinkedHashMap<>();

            for (Episode episode : user.getUsersHistory().getListenedEpisodes().keySet()) {
                if (episode.getOwner().equals(host.getName())) {
                    userEpisodes.put(episode, user.getUsersHistory().getListenedEpisodes().get(episode));
                }
            }

            for (Map.Entry<Episode, Integer> entry : userEpisodes.entrySet()) {
                Episode episode = entry.getKey();
                Integer count = entry.getValue();

                // If the episode is already in hostsEpisodes, add the count, otherwise put the new count
                hostsEpisodes.merge(episode, count, Integer::sum);
            }
        }

        List<Map.Entry<Episode, Integer>> top5Episodes = getTop5Episodes(hostsEpisodes);

        ObjectNode resultNodeEpisodes = objectMapper.createObjectNode();

        for (Map.Entry<Episode, Integer> entry : top5Episodes) {
            resultNodeEpisodes.put(entry.getKey().getName(), entry.getValue());
        }

        resultObjectNode.set("topEpisodes", resultNodeEpisodes);
        int listeners = host.getListeners();

        resultObjectNode.set("listeners", JsonNodeFactory.instance.numberNode(listeners));

        resultNode.set("result", resultObjectNode);

        return resultNode;
    }

    public static List<Map.Entry<String, Integer>> getTop5SongsList(LinkedHashMap<String, Integer> artistsSongs) {
        return artistsSongs.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int valueComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (valueComparison == 0) {
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                    return valueComparison;
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    public static List<Map.Entry<Album, Integer>> getTop5AlbumsList(LinkedHashMap<Album, Integer> artistsAlbums) {
        return artistsAlbums.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int valueComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (valueComparison == 0) {
                        return entry1.getKey().getName().compareTo(entry2.getKey().getName());
                    }
                    return valueComparison;
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    private LinkedHashMap<String, Integer> getTopSongs(final Artist artist, final Integer timestamp) {
        LinkedHashMap<String, Integer> artistsSongs = new LinkedHashMap<>();

        for (User user : artist.getListenersList()) {
            user.getPlayer().calculateStatus(timestamp);
            LinkedHashMap<Song, Integer> userSongs = new LinkedHashMap<>();

            for (Song song : user.getUsersHistory().getListenedSongs().keySet()) {
                if (song.getArtist().equals(artist.getName())) {
                    userSongs.put(song, user.getUsersHistory().getListenedSongs().get(song));
                }
            }

            for (Map.Entry<Song, Integer> entry : userSongs.entrySet()) {
                Song song = entry.getKey();
                Integer count = entry.getValue();

                // If the song is already in artistsSongs, add the count, otherwise put the new count
                artistsSongs.merge(song.getName(), count, Integer::sum);
            }
        }
        return artistsSongs;
    }

    public ObjectNode wrappedArtist(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        Artist artist = artists.get(command.getUsername());

        ObjectNode resultObjectNode = JsonNodeFactory.instance.objectNode();

        LinkedHashMap<Album, Integer> artistsAlbums = new LinkedHashMap<>();

        for (User user : artist.getListenersList()) {
            user.getPlayer().calculateStatus(command.getTimestamp());
            LinkedHashMap<Album, Integer> userAlbums = new LinkedHashMap<>();

            for (Album album : user.getUsersHistory().getListenedAlbums(true).keySet()) {
                if (album.getArtist().equals(artist.getName())) {
                    userAlbums.put(album, user.getUsersHistory().getListenedAlbums(true).get(album));
                }
            }

            for (Map.Entry<Album, Integer> entry : userAlbums.entrySet()) {
                Album album = entry.getKey();
                Integer count = entry.getValue();
                artistsAlbums.merge(album, count, Integer::sum);
            }
        }

        List<Map.Entry<Album, Integer>> top5Albums = getTop5AlbumsList(artistsAlbums);


        ObjectNode resultNodeAlbums = objectMapper.createObjectNode();
        for (Map.Entry<Album, Integer> entry : top5Albums) {
            resultNodeAlbums.put(entry.getKey().getName(), entry.getValue());
        }
        resultObjectNode.set("topAlbums", resultNodeAlbums);


        ObjectNode resultNodeSongs = objectMapper.createObjectNode();
        LinkedHashMap<String, Integer> artistsSongs = getTopSongs(artist, command.getTimestamp());

        List<Map.Entry<String, Integer>> top5Songs = getTop5SongsList(artistsSongs);

        for (Map.Entry<String, Integer> entry : top5Songs) {
            resultNodeSongs.put(entry.getKey(), entry.getValue());
        }

        resultObjectNode.set("topSongs", resultNodeSongs);

        LinkedHashMap<User, Integer> topFans = getTop5Fans(artist.getName(), command.getTimestamp());
        ArrayNode topFansArrayNode = objectMapper.createArrayNode();

        for (Map.Entry<User, Integer> entry : topFans.entrySet()) {
            topFansArrayNode.add(entry.getKey().getUsername());
        }

        resultObjectNode.set("topFans", topFansArrayNode);

        int listeners = artist.getListeners();

        resultObjectNode.set("listeners", JsonNodeFactory.instance.numberNode(listeners));

        resultNode.set("result", resultObjectNode);

        if (top5Albums.isEmpty() && top5Songs.isEmpty() && topFans.isEmpty() && listeners == 0) {
            resultNode = createResultNode(command);
            resultNode.put("message", "No data to show for artist " + command.getUsername() + ".");
            return resultNode;
        }

        return resultNode;
    }

    public ObjectNode buyPremium(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());
        user.getPlayer().calculateStatus(command.getTimestamp());

        if (user.isPremium()) {
            resultNode.put("message", command.getUsername() + " is already a premium user.");
            return resultNode;
        }

        user.buyPremium();
        resultNode.put("message", command.getUsername() + " bought the subscription successfully.");
        return resultNode;
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

    public void calculateRevenue(User user) {
        LinkedHashMap<String, Integer> listenedArtists = new LinkedHashMap<>();
        double totalListenedSongs = user.getUsersHistory().totalPremiumSongs();
        double listenedSongsArtist = 0;
        double songRevenue = 0;
        double premiumPrice = 1000000;


        for (Map.Entry<Song, Integer> entry : user.getUsersHistory().getListenedSongsPremium().entrySet()) {
            Artist artist = artists.get(entry.getKey().getArtist());
            listenedArtists = addArtists(artist.getName(), entry.getValue(), listenedArtists);
            double revenuePerSong = (premiumPrice / totalListenedSongs) * entry.getValue();
            entry.getKey().setRevenue(revenuePerSong);
        }

        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            if (listenedArtists.containsKey(entry.getValue().getName())) {
                listenedSongsArtist = listenedArtists.get(entry.getValue().getName());
                songRevenue = (premiumPrice / totalListenedSongs) * listenedSongsArtist;
                entry.getValue().addSongRevenue(songRevenue);
            }
        }
        user.getUsersHistory().deleteListenedSongPremium();
    }

    public ObjectNode cancelPremium(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());
        user.getPlayer().calculateStatus(command.getTimestamp());
        if (!user.isPremium()) {
            resultNode.put("message", command.getUsername() + " is not a premium user.");
            return resultNode;
        }

        user.cancelPremium();
        resultNode.put("message", command.getUsername() + " cancelled the subscription successfully.");

        calculateRevenue(user);

        return resultNode;
    }

    public void calculateAdBreak(final User user, final Integer price) {
        LinkedHashMap<String, Integer> listenedArtists = new LinkedHashMap<>();
        double totalListenedSongs = user.getPlayer().totalAdBreakSongs();
        double listenedSongsArtist = 0;
        double songRevenue = 0;

        for (Map.Entry<Song, Integer> entry : user.getPlayer().getSongsAdBreak().entrySet()) {
            Artist artist = artists.get(entry.getKey().getArtist());
            listenedArtists = addArtists(artist.getName(), entry.getValue(), listenedArtists);
            double revenuePerSong = (price / totalListenedSongs) * entry.getValue();
            entry.getKey().setRevenue(revenuePerSong);
        }

        for (Map.Entry<String, Artist> entry : artists.entrySet()) {
            if (listenedArtists.containsKey(entry.getValue().getName())) {
                listenedSongsArtist = listenedArtists.get(entry.getValue().getName());
                songRevenue = (price / totalListenedSongs) * listenedSongsArtist;
                entry.getValue().addSongRevenue(songRevenue);
            }
        }
        user.getPlayer().clearSongsAdBreak();
    }

    public ObjectNode adBreak(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (!artists.containsKey(command.getUsername())
                && !users.containsKey(command.getUsername())
                && !hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());
        user.getPlayer().calculateStatus(command.getTimestamp());

        if (user.getPlayer().getPlayMode().equals("clear")) {
            resultNode.put("message", command.getUsername()
                    + " is not playing any music.");
            return resultNode;
        }

        user.getPlayer().setAdBreak();
        user.getPlayer().setAdPrice(command.getPrice());

        resultNode.put("message", "Ad inserted successfully.");
        return resultNode;
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
     * Retrieves all the users
     *
     * @return A LinkedHashMap where the keys are user names and the values are User objects.
     */
    public LinkedHashMap<String, User> getUsers() {
        return users;
    }

    /**
     * Retrieves all the artists
     *
     * @return A LinkedHashMap where the keys are artist names and the values are Artist objects.
     */
    public LinkedHashMap<String, Artist> getArtists() {
        return artists;
    }

    /**
     * Retrieves all the hosts
     *
     * @return A LinkedHashMap where the keys are host names and the values are Host objects.
     */
    public LinkedHashMap<String, Host> getHosts() {
        return hosts;
    }

    /**
     * Retrieves all the albums that exist in the library
     *
     * @return An ArrayList of Playlist objects
     */
    public ArrayList<Album> getAllAlbums() {
        return allAlbums;
    }
}
