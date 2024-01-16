package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.users.artist.Artist;
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

    /**
     * Creates a result JSON node containing command details.
     * This method constructs a JSON node with command, user, and timestamp information.
     *
     * @param command The Command object containing details to be put in the result node.
     * @return ObjectNode The created result node with command details.
     */
    private ObjectNode createResultNode(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        return resultNode;
    }

    /**
     * Generates a song recommendation for a specific user.
     * This method selects a random song based on the user's player settings and updates the
     * user's home page with this recommendation.
     *
     * @param command The Command object containing user details and other relevant information.
     * @param users   A map of all users by their usernames.
     * @param artists A map of all artists by their names.
     * @param hosts   A map of all hosts by their names.
     * @param songs   An ArrayList of all available songs.
     * @return ObjectNode A JSON node containing the result of the recommendation
     * generation, including a status message.
     */
    @Override
    public ObjectNode generateRecommendation(final Command command,
                                             final Map<String, User> users,
                                             final Map<String, Artist> artists,
                                             final Map<String, Host> hosts,
                                             final ArrayList<Song> songs) {
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
            resultNode.put("message", "The recommendations for user "
                    + command.getUsername() + " have been updated successfully.");
        }

        return resultNode;
    }
}
