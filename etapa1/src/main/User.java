package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class User implements SearchBarCommands, PlaylistCommands, PlayerCommands {
    private String username;
    private final int age;
    private final String city;
    private ArrayList<Playlist> playlists;
    private Command lastCommandSearch;
    private Command lastCommand;
    private ArrayList<String> lastCommandResult;
    private boolean isSearch;
    private boolean isSelect;
    private final SearchBar searchBar;
    private final ArrayList<Playlist> allPlaylists;
    private final ArrayList<Playlist> followingPlaylists;
    private final Player player;
    private final ObjectMapper objectMapper;

    public User(final String username, final int age, final String city,
                final ArrayList<Song> songs, final ArrayList<Podcast> podcasts,
                final ArrayList<Playlist> allPlaylists) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.playlists = new ArrayList<>();
        this.searchBar = new SearchBar(songs, podcasts, allPlaylists, playlists);
        this.allPlaylists = allPlaylists;
        this.followingPlaylists = new ArrayList<>();
        this.player = new Player();
        isSearch = false;
        isSelect = false;
        objectMapper = new ObjectMapper();
    }

    /**
     * Create and add common properties to the result node
     *
     * @param command The received command
     * @return ObjectNode with the common properties of the commands
     */
    private ObjectNode createResultNode(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        return resultNode;
    }

    /**
     * Performs a search based on the provided command and constructs a response object
     *
     * @param command The search command containing the necessary information.
     * @return An ObjectNode representing the search results and associated information.
     */
    public ObjectNode search(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        // Update various tracking variables with the latest search command
        lastCommandSearch = command;
        lastCommand = command;
        isSearch = true;
        lastCommandResult = searchBar.search(command);

        player.resetPlayer(command.getTimestamp());

        resultNode.put("message",
                "Search returned " + lastCommandResult.size() + " results");

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (String song : lastCommandResult) {
            resultsArray.add(song);
        }

        resultNode.set("results", resultsArray);
        return resultNode;
    }

    /**
     * Handles the selection of an item based on the provided command in the context
     * of a search operation.
     * Updates internal state and returns a response ObjectNode
     *
     * @param command The command containing the necessary information for select method
     * @return An ObjectNode representing the result of the selection operation
     */
    public ObjectNode select(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (isSearch) {
            if (!lastCommandResult.isEmpty()) {
                if (lastCommandResult.size() >= command.getItemNumber()) {
                    resultNode.put("message", "Successfully selected "
                            + lastCommandResult.get(command.getItemNumber() - 1) + ".");
                    String result = lastCommandResult.get(command.getItemNumber() - 1);
                    lastCommandResult.clear();
                    lastCommandResult.add(result);
                    isSelect = true;
                    isSearch = false;
                    lastCommand = command;
                } else {
                    resultNode.put("message", "The selected ID is too high.");
                    lastCommandResult.clear();
                    isSearch = false;
                }
            } else {
                resultNode.put("message", "The selected ID is too high.");
            }
        } else {
            resultNode.put("message",
                    "Please conduct a search before making a selection.");
            lastCommandResult.clear();
            isSearch = false;
        }
        return resultNode;
    }

    /**
     * Finds a playlist in the collection based on its name
     *
     * @param name The name of the playlist to find.
     * @return The Playlist object with the specified name, or null if not found.
     */
    private Playlist findPlaylist(final String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }

    /**
     * Loads the songs from a specified playlist into the player
     *
     * @param command    The command containing information about the load operation.
     * @param resultNode The ObjectNode to store the result of the load operation.
     * @param playlist   The playlist to be loaded.
     */
    private void loadPlaylist(final Command command,
                              final ObjectNode resultNode,
                              final Playlist playlist) {
        if (playlist.getSongs().isEmpty()) {
            resultNode.put("message", "You can't load an empty audio collection!");
        } else {
            player.load(playlist, command.getTimestamp());
            resultNode.put("message", "Playback loaded successfully.");
        }
    }

    /**
     * Loads content: song, playlist, or podcast into the audio player based on the provided
     * command and library
     *
     * @param command The command containing information about the load operation.
     * @param library The library containing songs, playlists, and podcasts.
     * @return An ObjectNode representing the result of the load operation.
     */
    public ObjectNode load(final Command command, final Library library) {
        ObjectNode resultNode = createResultNode(command);

        if (!isSelect) {
            resultNode.put("message",
                    "Please select a source before attempting to load.");
            return resultNode;
        }
        if (lastCommandResult.isEmpty()) {
            resultNode.put("message", "You can't load an empty audio collection!");
            return resultNode;
        }
        isSelect = false;
        lastCommand = command;
        switch (lastCommandSearch.getType()) {
            case ("song"):
                Song song = library.findSong(lastCommandResult.get(0));
                player.load(song, command.getTimestamp());
                break;
            case ("playlist"):
                Playlist playlist = findPlaylist(lastCommandResult.get(0));
                if (playlist == null) {
                    playlist = library.findPlaylist(lastCommandResult.get(0));
                    assert playlist != null;
                }
                loadPlaylist(command, resultNode, playlist);
                return resultNode;
            case ("podcast"):
                Podcast podcast = library.findPodcast(lastCommandResult.get(0));
                assert podcast != null;
                player.load(podcast, command.getTimestamp());
                break;
            default:
                break;
        }
        resultNode.put("message", "Playback loaded successfully.");
        return resultNode;
    }

    /**
     * This method is used to play or pause the current song.
     *
     * @param command The command containing information about the load operation.
     * @return An ObjectNode representing the result of the play/pause operation.
     */
    public ObjectNode playPause(final Command command) {
        ObjectNode resultNode = createResultNode(command);
        lastCommand = command;

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message",
                    "Please load a source before attempting to pause or resume playback.");
            return resultNode;
        }
        if (player.isPaused()) {
            resultNode.put("message",
                    "Playback resumed successfully.");
        } else {
            resultNode.put("message",
                    "Playback paused successfully.");
        }
        player.playPause(command.getTimestamp());
        return resultNode;
    }

    /**
     * This method is used to change the repeat status of the player.
     *
     * @param command The command containing information about the repeat operation.
     * @return An ObjectNode representing the result of the repeat operation.
     */
    public ObjectNode repeat(final Command command) {
        ObjectNode resultNode = createResultNode(command);
        lastCommand = command;

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message",
                    "Please load a source before setting the repeat status.");
            return resultNode;
        }

        resultNode.put("message",
                "Repeat mode changed to " + player.repeat(command.getTimestamp()) + ".");
        return resultNode;
    }

    /**
     * This method is used to change the shuffle status of the current playlist.
     *
     * @param command The command containing information about the shuffle operation.
     * @return An ObjectNode representing the result of the shuffle operation.
     */
    public ObjectNode shuffle(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        player.shuffle(command, resultNode);
        return resultNode;
    }

    /**
     * This method is used to add or decrease 90s from the curent song.
     *
     * @param command The command containing information about the forward/backward operation.
     * @return An ObjectNode representing the result of the forward/backward operation.
     */
    public ObjectNode forwardBackward(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (player.getPlayMode().equals("clear") && command.getCommand().equals("forward")) {
            resultNode.put("message",
                    "Please load a source before attempting to forward.");
            return resultNode;
        } else if (player.getPlayMode().equals("clear")
                && command.getCommand().equals("backward")) {
            resultNode.put("message", "Please select a source before rewinding.");
            return resultNode;
        }

        if (!player.getPlayMode().equals("podcast")) {
            resultNode.put("message", "The loaded source is not a podcast.");
            return resultNode;
        }
        player.forwardBackward(command);

        if (command.getCommand().equals("forward")) {
            resultNode.put("message", "Skipped forward successfully.");
        } else if (command.getCommand().equals("backward")) {
            resultNode.put("message", "Rewound successfully.");
        }

        return resultNode;
    }

    /**
     * This method is used to like or unlike the current song.
     *
     * @param command The command containing information about the like/unlike operation.
     * @return An ObjectNode representing the result of the like/unlike operation.
     */
    public ObjectNode like(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message",
                    "Please load a source before liking or unliking.");
            return resultNode;
        }
        if (player.getPlayMode().equals("podcast")) {
            resultNode.put("message", "Loaded source is not a song.");
            return resultNode;
        }
        if (player.like(command)) {
            resultNode.put("message", "Like registered successfully.");
        } else {
            resultNode.put("message", "Unlike registered successfully.");
        }

        lastCommand = command;
        return resultNode;
    }

    /**
     * This method is used to get to the next/previous song.
     *
     * @param command The command containing information about the next/prev operation.
     * @return An ObjectNode representing the result of the next/prev operation.
     */
    public ObjectNode nextPrev(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (command.getCommand().equals("next")) {
            if (player.getPlayMode().equals("clear")) {
                resultNode.put("message",
                        "Please load a source before skipping to the next track.");
                return resultNode;
            }
            player.next(command, resultNode);
        } else {
            if (player.getPlayMode().equals("clear")) {
                resultNode.put("message",
                        "Please load a source before returning to the previous track.");
                return resultNode;
            }

            player.prev(command, resultNode);
            if (player.isPaused()) {
                player.setPaused(false);
            }
        }

        return resultNode;
    }

    /**
     * This method is used to get to the next/previous song.
     *
     * @param command The command containing information about the add/remove song
     *                in a playlist operation.
     * @return An ObjectNode representing the result of the add/remove song
     * in a playlist operation.
     */
    public ObjectNode addRemoveInPlaylist(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message",
                    "Please load a source before adding to or removing from the playlist.");
            return resultNode;
        }
        if (!player.getPlayMode().equals("song")) {
            resultNode.put("message", "The loaded source is not a song.");
            return resultNode;
        }
        if (command.getPlaylistId() > playlists.size()) {
            resultNode.put("message", "The specified playlist does not exist.");
            return resultNode;
        }
        if (playlists.get(command.getPlaylistId() - 1).getSongs()
                .contains(player.getCurrentSong())) {
            playlists.get(command.getPlaylistId() - 1).getSongs().remove(player.getCurrentSong());
            resultNode.put("message", "Successfully removed from playlist.");
        } else {
            playlists.get(command.getPlaylistId() - 1).getSongs().add(player.getCurrentSong());
            resultNode.put("message", "Successfully added to playlist.");
        }

        return resultNode;
    }

    /**
     * This method is used to get the information about the state of the player
     *
     * @param command The command containing information about the status operation.
     * @return An ObjectNode representing the result of the status operation.
     */
    public ObjectNode status(final Command command) {
        ObjectNode resultNode = createResultNode(command);
        resultNode.set("stats", player.status(command.getTimestamp()));

        lastCommand = command;
        return resultNode;
    }

    /**
     * This method is used to create a new playlist.
     *
     * @param command The command containing information about the create playlist operation.
     * @return An ObjectNode representing the result of the create playlist operation.
     */
    public ObjectNode createPlaylist(final Command command) {
        Playlist newPlaylist = new Playlist(command.getPlaylistName(), username);

        ObjectNode resultNode = createResultNode(command);

        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(newPlaylist.getName())) {
                resultNode.put("message",
                        "A playlist with the same name already exists.");
                return resultNode;
            }
        }
        playlists.add(newPlaylist);
        allPlaylists.add(newPlaylist);
        resultNode.put("message", "Playlist created successfully.");

        lastCommand = command;

        return resultNode;
    }

    /**
     * This method is used to switch the visibility of a playlist.
     * If the playlist is public, it will be switched to private and vice versa.
     *
     * @param command The command containing information about the switch visibility operation.
     * @return An ObjectNode representing the result of switch visibility operation.
     */
    public ObjectNode switchVisibility(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (command.getPlaylistId() > playlists.size()) {
            resultNode.put("message", "The specified playlist ID is too high.");
        } else {
            Playlist playlist = playlists.get(command.getPlaylistId() - 1);
            if (playlist.isVisibility()) {
                playlist.setVisibility(false);
            } else {
                for (Playlist playlist1 : allPlaylists) {
                    if (playlist1.getName().equals(playlist.getName())
                            && playlist1.getOwner().equals(playlist.getOwner())) {
                        playlist1.setVisibility(true);
                    }
                }
                playlist.setVisibility(true);
            }
            if (playlist.isVisibility()) {
                resultNode.put("message",
                        "Visibility status updated successfully to public.");
            } else {
                resultNode.put("message",
                        "Visibility status updated successfully to private.");
            }
        }
        lastCommand = command;
        return resultNode;
    }

    /**
     * This method is used to follow or unfollow a playlist.
     *
     * @param command The command containing information about the follow/unfollow operation.
     * @return An ObjectNode representing the result of follow/unfollow operation.
     */
    public ObjectNode followPlaylist(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        if (lastCommand != null) {
            if (!lastCommand.getCommand().equals("select")) {
                resultNode.put("message",
                        "Please select a source before following or unfollowing.");
            } else if (!lastCommandSearch.getType().equals("playlist")) {
                resultNode.put("message",
                        "The selected source is not a playlist.");
            } else if (!lastCommandResult.isEmpty()) {
                String playlistName = lastCommandResult.get(0);
                for (Playlist playlist : allPlaylists) {
                    if (playlist.getName().equals(playlistName) && playlist.isVisibility()) {
                        if (playlist.getOwner().equals(command.getUsername())) {
                            resultNode.put("message",
                                    "You cannot follow or unfollow your own playlist.");
                        } else {
                            if (!followingPlaylists.contains(playlist)) {
                                followingPlaylists.add(playlist);
                                playlist.follow();
                                resultNode.put("message",
                                        "Playlist followed successfully.");
                                lastCommand = command;
                            } else {
                                followingPlaylists.remove(playlist);
                                playlist.unfollow();
                                resultNode.put("message",
                                        "Playlist unfollowed successfully.");
                                lastCommand = command;
                            }
                        }
                    } else if (playlist.getName().equals(playlistName)
                            && playlist.getOwner().equals(command.getUsername())) {
                        resultNode.put("message",
                                "You cannot follow or unfollow your own playlist.");
                    }
                }
            }
        } else {
            resultNode.put("message",
                    "Please select a source before following or unfollowing.");
        }

        return resultNode;
    }

    /**
     * This method is used to get information about the content of all playlists owned by the user.
     *
     * @param command The command containing information about the show playlists operation.
     * @return An ObjectNode representing the result of the show playlists operation.
     */
    public ObjectNode showPlaylists(final Command command) {
        ObjectNode resultNode = createResultNode(command);
        ArrayNode resultField = objectMapper.createArrayNode();

        for (Playlist playlist : playlists) {
            ObjectNode newNode = objectMapper.createObjectNode();
            newNode.put("name", playlist.getName());

            ArrayNode resultsArray = objectMapper.createArrayNode();
            if (playlist.getSongs() != null) {
                if (playlist == player.getCurrentPlaylist() && player.isShuffle()) {
                    for (Song song : player.getUnsuffledSongs()) {
                        resultsArray.add(song.getName());
                    }
                } else {
                    for (Song song : playlist.getSongs()) {
                        resultsArray.add(song.getName());
                    }
                }

            }
            newNode.set("songs", resultsArray);
            if (playlist.isVisibility()) {
                newNode.put("visibility", "public");
            } else {
                newNode.put("visibility", "private");
            }
            newNode.put("followers", playlist.getFollowers());
            resultField.add(newNode);
        }

        lastCommand = command;
        resultNode.set("result", resultField);
        return resultNode;
    }

    /**
     * This method is used to the songs that are liked by the user.
     *
     * @param command The command containing information about the show preferred songs operation.
     * @return An ObjectNode representing the result of the show preferred songs operation.
     */
    public ObjectNode showPreferredSongs(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (Song song : player.getLikedSongs()) {
            resultsArray.add(song.getName());
        }
        resultNode.set("result", resultsArray);

        lastCommand = command;
        return resultNode;
    }

    /**
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }
}
