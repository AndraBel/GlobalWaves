package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.audioFiles.audioCollection.Playlist;
import app.users.arist.Artist;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RandomPlaylistRecommendation implements RecommendationStrategy {
    private final ObjectMapper objectMapper;

    public RandomPlaylistRecommendation() {
        objectMapper = new ObjectMapper();
    }

    private ObjectNode createResultNode(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        return resultNode;
    }
    @Override
    public ObjectNode generateRecommendation(Command command, Map<String, User> users, Map<String, Artist> artists,
                                             Map<String, Host> hosts, ArrayList<Song> songs) {
        ObjectNode resultNode = createResultNode(command);

        if (!users.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());

        List<Map.Entry<String, Integer>> top3Genres = user.getTopGenres();

        Playlist newPlaylist = new Playlist(command.getUsername() + "'s recommendations", command.getUsername());

        int index = 1;
        ArrayList<Song> songs1 = new ArrayList<>();
        ArrayList<Song> songs2 = new ArrayList<>();
        ArrayList<Song> songs3 = new ArrayList<>();

        for (Map.Entry<String, Integer> genre : top3Genres) {
            if (index == 1) {
                for (Song song : songs) {
                    if (song.getGenre().equals(genre.getKey()) && !songs1.contains(song)) {
                        songs1.add(song);
                    }
                }
            } else if (index == 2) {
                for (Song song : songs) {
                    if (song.getGenre().equals(genre.getKey()) && !songs2.contains(song)) {
                        songs2.add(song);
                    }
                }
            } else {
                for (Song song : songs) {
                    if (song.getGenre().equals(genre.getKey()) && !songs3.contains(song)) {
                        songs3.add(song);
                    }
                }
            }
            index++;
        }

        songs1 = songs1.stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));

        songs2 = songs2.stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .limit(3)
                .collect(Collectors.toCollection(ArrayList::new));

        songs3 = songs3.stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .limit(2)
                .collect(Collectors.toCollection(ArrayList::new));

        for (Song song : songs1) {
            newPlaylist.getSongs().add(song);
        }
        for (Song song : songs2) {
            newPlaylist.getSongs().add(song);
        }
        for (Song song : songs3) {
            newPlaylist.getSongs().add(song);
        }

        user.getPlaylists().add(newPlaylist);
        user.getHomePage().getRecommandedPlaylists().add(newPlaylist);
        user.getHomePage().setLastRecommandation("playlist");

        resultNode.put("message", "The recommendations for user " + command.getUsername() +
                " have been updated successfully.");

        return resultNode;
    }
}
