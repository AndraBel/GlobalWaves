package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.audioFiles.audioCollection.Playlist;
import app.users.artist.Artist;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FansPlaylistRecommendation implements RecommendationStrategy {
    private final ObjectMapper objectMapper;
    private final LinkedHashMap<String, User> users;

    public FansPlaylistRecommendation(final LinkedHashMap<String, User> users) {
        objectMapper = new ObjectMapper();
        this.users = users;
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
     * Retrieves the top 5 fans of a specific artist based on the number of times they've
     * listened to the artist.
     *
     * @param artistName The name of the artist for whom to find the top fans.
     * @param timestamp  The timestamp to consider for calculating user listen counts.
     * @return LinkedHashMap<User, Integer> A map of top 5 fans and their
     * listen counts for the artist.
     */
    public LinkedHashMap<User, Integer> getTop5Fans(final String artistName,
                                                    final Integer timestamp) {
        LinkedHashMap<User, Integer> topFans = new LinkedHashMap<>();

        for (Map.Entry<String, User> entry : users.entrySet()) {
            entry.getValue().getPlayer().calculateStatus(timestamp);
            for (Map.Entry<String, Integer> artist : entry.getValue()
                    .getUsersHistory().getListenedArtists().entrySet()) {
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

    /**
     * Generates a recommendation playlist for a user based on the current song and
     * the top fans of the song's artist.
     *
     * @param command        The Command object containing user details and other
     *                       relevant information.
     * @param searchedUsers  Map of all searched users.
     * @param artists        Map of all artists.
     * @param hosts          Map of all hosts.
     * @param songs          ArrayList of all available songs.
     * @return ObjectNode    Result node with the status of the recommendation generation.
     */
    @Override
    public ObjectNode generateRecommendation(final Command command,
                                             final Map<String, User> searchedUsers,
                                             final Map<String, Artist> artists,
                                             final Map<String, Host> hosts,
                                             final ArrayList<Song> songs) {
        ObjectNode resultNode = createResultNode(command);

        if (artists.containsKey(command.getUsername())
                || hosts.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " is not a normal user.");
            return resultNode;
        }

        if (!searchedUsers.containsKey(command.getUsername())) {
            resultNode.put("message", "The username " + command.getUsername()
                    + " doesn't exist.");
            return resultNode;
        }

        User user = searchedUsers.get(command.getUsername());

        Song currentSong = user.getPlayer().getCurrentSong();

        LinkedHashMap<User, Integer> sortedTop5Fans = getTop5Fans(currentSong.getArtist(),
                command.getTimestamp());

        Playlist newPlaylist = new Playlist(currentSong.getArtist()
                + " Fan Club recommendations",
                command.getUsername());

        for (Map.Entry<User, Integer> entry : sortedTop5Fans.entrySet()) {
            List<Song> first5LikedSongs = user.getHomePage().getFirst5LikedSongs();
            for (Song song : first5LikedSongs) {
                newPlaylist.getSongs().add(song);
            }
        }

        user.getPlaylists().add(newPlaylist);
        user.getHomePage().getRecommandedPlaylists().add(newPlaylist);
        user.getHomePage().setLastRecommandation("playlist");

        resultNode.put("message", "The recommendations for user "
                + command.getUsername() + " have been updated successfully.");

        return resultNode;
    }
}
