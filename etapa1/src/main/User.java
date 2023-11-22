package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class User {
    private String username;
    private int age;
    private String city;
    private ArrayList<Playlist> playlists;
    private Command lastCommandSearch;
    private Command lastCommand;
    private ArrayList<String> lastCommandResult;
    private boolean isSearch;
    private boolean isSelect;
    private SearchBar searchBar;
    private ArrayList<Playlist> allPlaylists;
    private ArrayList<Playlist> followingPlaylists;
    private Player player;

    public User(String username, int age, String city, ArrayList<Song> songs, ArrayList<Podcast> podcasts, ArrayList<Playlist> allPlaylists) {
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
    }

    public ObjectNode search(Command command) {
        lastCommandSearch = command;
        lastCommand = command;
        isSearch = true;
        lastCommandResult = searchBar.Search(command);

        player.resetPlayer(command.getTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        // Add properties to the ObjectNode
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        resultNode.put("message", "Search returned " + (lastCommandResult).size() + " results");

        // Add results as an ArrayNode
        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (String song : lastCommandResult) {
            resultsArray.add(song);
        }

        resultNode.set("results", resultsArray);
        return resultNode;
    }

    public ObjectNode select(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (isSearch) {
            if (!lastCommandResult.isEmpty()) {
                if (lastCommandResult.size() >= command.getItemNumber()) {
                    resultNode.put("message", "Successfully selected " + lastCommandResult.get(command.getItemNumber() - 1) + ".");
                    String result = lastCommandResult.get(command.getItemNumber() - 1);
                    lastCommandResult.clear();
                    lastCommandResult.add(result);
                    isSelect = true;
                } else {
                    resultNode.put("message", "The selected ID is too high.");
                    lastCommandResult.clear();
                    isSearch = false;
                    return resultNode;
                }
            } else {
                resultNode.put("message", "The selected ID is too high.");
                return resultNode;
            }
        } else {
            resultNode.put("message", "Please conduct a search before making a selection.");
            lastCommandResult.clear();
            isSearch = false;
            return resultNode;
        }
        isSearch = false;
        lastCommand = command;

        return resultNode;
    }

    public ObjectNode createPlaylist(Command command) {
        Playlist newPlaylist = new Playlist(command.getPlaylistName(), username);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(newPlaylist.getName())) {
                resultNode.put("message", "A playlist with the same name already exists.");
                return resultNode;
            }
        }
        playlists.add(newPlaylist);
        allPlaylists.add(newPlaylist);
        resultNode.put("message", "Playlist created successfully.");

        lastCommand = command;

        return resultNode;
    }

    public ObjectNode switchVisibility(Command command) {
        int playlistId = command.getPlaylistId();

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (playlistId > playlists.size()) {
            resultNode.put("message", "The specified playlist ID is too high.");
        } else {
            Playlist playlist = playlists.get(playlistId - 1);
            if (playlist.getVisibility()) {
                playlist.setVisibility(false);
            } else {
                for (Playlist playlist1 : allPlaylists) {
                    if (playlist1.getName().equals(playlist.getName()) && playlist1.getOwner().equals(playlist.getOwner())) {
                        playlist1.setVisibility(true);
                    }
                }
                playlist.setVisibility(true);
            }
            if (playlist.getVisibility()) {
                resultNode.put("message", "Visibility status updated successfully to public.");
            } else {
                resultNode.put("message", "Visibility status updated successfully to private.");
            }
        }
        lastCommand = command;
        return resultNode;
    }

    public ObjectNode follow(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (lastCommand != null) {
            if (!lastCommand.getCommand().equals("select")) {
                resultNode.put("message", "Please select a source before following or unfollowing.");
            } else if (!lastCommandSearch.getType().equals("playlist")) {
                resultNode.put("message", "The selected source is not a playlist.");
            } else if (!lastCommandResult.isEmpty()) {
                String playlistName = lastCommandResult.get(0);
                for (Playlist playlist : allPlaylists) {
                    if (playlist.getName().equals(playlistName) && playlist.getVisibility()) {
                        if (playlist.getOwner().equals(command.getUsername())) {
                            resultNode.put("message", "You cannot follow or unfollow your own playlist.");
                        } else {
                            if (!followingPlaylists.contains(playlist)) {
                                followingPlaylists.add(playlist);
                                playlist.follow();
                                resultNode.put("message", "Playlist followed successfully.");
                                lastCommand = command;
                            } else {
                                followingPlaylists.remove(playlist);
                                playlist.unfollow();
                                resultNode.put("message", "Playlist unfollowed successfully.");
                                lastCommand = command;
                            }
                        }
                    } else if (playlist.getName().equals(playlistName) && playlist.getOwner().equals(command.getUsername())) {
                        resultNode.put("message", "You cannot follow or unfollow your own playlist.");
                    }
                }
            }
        } else {
            resultNode.put("message", "Please select a source before following or unfollowing.");
        }

        return resultNode;
    }

    public ObjectNode showPlaylists(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        ArrayNode resultField = objectMapper.createArrayNode();

        for (Playlist playlist : playlists) {
            ObjectNode newNode = objectMapper.createObjectNode();
            newNode.put("name", playlist.getName());

            ArrayNode resultsArray = objectMapper.createArrayNode();
            if (playlist.getSongs() != null) {
                for (Song song : playlist.getSongs()) {
                    resultsArray.add(song.getName());
                }
            }
            newNode.put("songs", resultsArray);
            if (playlist.getVisibility()) {
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

    public ObjectNode load(Command command, Library library) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (!isSelect) {
            resultNode.put("message", "Please select a source before attempting to load.");
            return resultNode;
        }
        if (lastCommandResult.isEmpty()) {
            resultNode.put("message", "You can't load an empty audio collection!");
            return resultNode;
        }
        isSelect = false;
        lastCommand = command;
        int index;
        switch (lastCommandSearch.getType()) {
            case ("song"):
                index = 0;
                for (Song song : library.getSongs()) {
                    if (song.getName().equals(lastCommandResult.get(0))) {
                        break;
                    }
                    index++;
                }
                player.load(library.getSongs().get(index), command.getTimestamp());
                break;
            case ("playlist"):
                for (Playlist playlist : playlists) {
                    if (playlist.getName().equals(lastCommandResult.get(0))) {
                        if (playlist.getSongs().isEmpty()) {
                            resultNode.put("message", "You can't load an empty audio collection!");
                            return resultNode;
                        }
                        player.load(playlist, command.getTimestamp());
                        resultNode.put("message", "Playback loaded successfully.");
                        return resultNode;
                    }
                }
                for (Playlist playlist : allPlaylists) {
                    if (playlist.getName().equals(lastCommandResult.get(0)) && playlist.getVisibility()) {
                        if (playlist.getSongs().isEmpty()) {
                            resultNode.put("message", "You can't load an empty audio collection!");
                            return resultNode;
                        }
                        player.load(playlist, command.getTimestamp());
                        resultNode.put("message", "Playback loaded successfully.");
                        return resultNode;
                    }
                }
                break;
            case ("podcast"):
                index = 0;
                for (Podcast podcast : library.getPodcasts()) {
                    if (podcast.getName().equals(lastCommandResult.get(0))) {
                        break;
                    }
                    index++;
                }
                player.load(library.getPodcasts().get(index), command.getTimestamp());
                break;
        }
        resultNode.put("message", "Playback loaded successfully.");
        return resultNode;
    }

    public ObjectNode status(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        resultNode.put("stats", player.status(command.getTimestamp()));

        lastCommand = command;
        return resultNode;
    }

    public ObjectNode playPause(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        lastCommand = command;

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message", "Please load a source before attempting to pause or resume playback.");
            return resultNode;
        }
        if (player.isPaused()) {
            resultNode.put("message", "Playback resumed successfully.");
        } else {
            resultNode.put("message", "Playback paused successfully.");
        }
        player.playPause(command.getTimestamp());
        return resultNode;
    }

    public ObjectNode repeat(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        lastCommand = command;

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message", "Please load a source before setting the repeat status.");
            return resultNode;
        }

        resultNode.put("message", "Repeat mode changed to " + player.repeat(command.getTimestamp()) + ".");
        return resultNode;
    }

    public ObjectNode addRemoveInPlaylist(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message", "Please load a source before adding to or removing from the playlist.");
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
        if (playlists.get(command.getPlaylistId() - 1).getSongs().contains(player.getCurrentSong())) {
            playlists.get(command.getPlaylistId() - 1).getSongs().remove(player.getCurrentSong());
            resultNode.put("message", "Successfully removed from playlist.");
        } else {
            playlists.get(command.getPlaylistId() - 1).getSongs().add(player.getCurrentSong());
            resultNode.put("message", "Successfully added to playlist.");
        }

        return resultNode;
    }

    public ObjectNode like(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (player.getPlayMode().equals("clear")) {
            resultNode.put("message", "Please load a source before liking or unliking.");
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

    public ObjectNode showPreferredSongs(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (Song song : player.getLikedSongs()) {
            resultsArray.add(song.getName());
        }
        resultNode.set("result", resultsArray);

        lastCommand = command;
        return resultNode;
    }

    public ObjectNode nextPrev(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (command.getCommand().equals("next")) {
            if (player.getPlayMode().equals("clear")) {
                resultNode.put("message", "Please load a source before skipping to the next track.");
                return resultNode;
            }
            player.next(command, resultNode);
        } else {
            if (player.getPlayMode().equals("clear")) {
                resultNode.put("message", "Please load a source before returning to the previous track.");
                return resultNode;
            }

            player.prev(command, resultNode);
            if (player.isPaused())
                player.setPaused(false);
        }

        return resultNode;
    }

    public ObjectNode forwardBackword(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        if (player.getPlayMode().equals("clear") && command.getCommand().equals("forward")) {
            resultNode.put("message", "Please load a source before attempting to forward.");
            return resultNode;
        } else if (player.getPlayMode().equals("clear") && command.getCommand().equals("backward")) {
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

    public ObjectNode shuffle(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());

        player.shuffle(command, resultNode);
        return resultNode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Command getLast_command() {
        return lastCommand;
    }

    public void setLast_command(Command last_command) {
        this.lastCommand = last_command;
    }

    public Object getLast_command_result() {
        return lastCommandResult;
    }

    public void setLast_command_result(ArrayList<String> last_command_result) {
        this.lastCommandResult = last_command_result;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public void setSearchBar(SearchBar searchBar) {
        this.searchBar = searchBar;
    }
}
