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
    private SearchBar searchBar;
    private ArrayList<Playlist> publicPlaylist;
    private ArrayList<Playlist> followingPlaylists;
    private Player player;

    public User(String username, int age, String city, ArrayList<Song> songs, ArrayList<Podcast> podcasts, ArrayList<Playlist> publicPlaylists) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.playlists = new ArrayList<>();
        this.searchBar = new SearchBar(songs, podcasts, publicPlaylists, playlists);
        this.publicPlaylist = publicPlaylists;
        this.followingPlaylists = new ArrayList<>();
        this.player = new Player();
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
                } else {
                    resultNode.put("message", "The selected ID is too high.");
                    lastCommandResult.clear();
                    isSearch = false;
                    return resultNode;
                }
            }else {
                resultNode.put("message", "The selected ID is too high.");
                return  resultNode;
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
        publicPlaylist.add(newPlaylist);
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
                publicPlaylist.remove(playlist);
                playlist.setVisibility(false);
            } else {
                playlist.setVisibility(true);
                publicPlaylist.add(playlist);
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
                for (Playlist playlist : publicPlaylist) {
                    if (playlist.getName().equals(playlistName)) {
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

        if (!lastCommand.getCommand().equals("select")) {
            resultNode.put("message", "Please select a source before attempting to load.");
            return resultNode;
        }
        if (lastCommandResult.isEmpty()) {
            resultNode.put("message", "You can't load an empty audio collection!");
            return resultNode;
        }

        lastCommand = command;

        switch (lastCommandSearch.getType()) {
            case ("song"):
                int index = 0;
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
                for (Playlist playlist : publicPlaylist) {
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
