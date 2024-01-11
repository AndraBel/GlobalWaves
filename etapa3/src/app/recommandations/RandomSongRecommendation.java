package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.users.arist.Artist;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Map;

public class RandomSongRecommendation implements RecommendationStrategy {
    private final ObjectMapper objectMapper;

    public RandomSongRecommendation() {
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
    public ObjectNode generateRecommendation(Command command, Map<String, User> users,
                                             Map<String, Artist> artists, Map<String, Host> hosts,
                                             ArrayList<Song> songs) {
        ObjectNode resultNode = createResultNode(command);

        if (!users.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = users.get(command.getUsername());

        Song randomSong = user.getPlayer().randomSong(songs, command.getTimestamp());

        if (randomSong == null) {
            resultNode.put("message", "No song found.");
            return resultNode;
        } else {
            user.getHomePage().addRecommandedSong(randomSong);
            user.getHomePage().setLastRecommandation("song");
            resultNode.put("message", "The recommendations for user " + command.getUsername()
                    + " have been updated successfully.");
        }

        return resultNode;
    }
}
