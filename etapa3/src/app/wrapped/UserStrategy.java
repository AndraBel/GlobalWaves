package app.wrapped;

import app.admin.Command;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.podcasts.Episode;
import app.users.Host;
import app.users.artist.Artist;
import app.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserStrategy implements AllUsersStrategy {
    private final ObjectMapper objectMapper;
    private LinkedHashMap<String, User> users;
    private LinkedHashMap<String, Artist> artists;
    private LinkedHashMap<String, Host> hosts;

    public UserStrategy(final LinkedHashMap<String, User> users,
                        final LinkedHashMap<String, Artist> artists,
                        final LinkedHashMap<String, Host> hosts) {
        this.users = users;
        this.artists = artists;
        this.hosts = hosts;
        objectMapper = new ObjectMapper();
    }

    private ObjectNode createResultNode(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        return resultNode;
    }

    /***
     * Calculate result for wrapped command
     * @param command
     * @return objectNode containing the result for wrapped command
     */
    @Override
    public ObjectNode wrapped(final Command command) {
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

        if (user.getUsersHistory().getTopArtists(command.getTimestamp()).isEmpty()
                && user.getUsersHistory().getTopGenres(command.getTimestamp()).isEmpty()
                && user.getUsersHistory().getTopSongs(command.getTimestamp()).isEmpty()
                && user.getUsersHistory().getTopAlbums(command.getTimestamp(), false)
                .isEmpty() && user.getUsersHistory().getTopEpisodes().isEmpty()) {

            resultNode.put("message", "No data to show for user "
                    + command.getUsername() + ".");
            return resultNode;
        }

        List<Map.Entry<String, Integer>> topArtist = user.getUsersHistory()
                .getTopArtists(command.getTimestamp());
        for (Map.Entry<String, Integer> entry : topArtist) {
            resultNodeArtist.put(entry.getKey(), entry.getValue());
        }

        resultObjectNode.set("topArtists", resultNodeArtist);

        ObjectNode topGenresArray = objectMapper.createObjectNode();

        List<Map.Entry<String, Integer>> topGenres = user.getUsersHistory()
                .getTopGenres(command.getTimestamp());
        for (Map.Entry<String, Integer> entry : topGenres) {
            topGenresArray.put(entry.getKey(), entry.getValue());
        }
        resultObjectNode.set("topGenres", topGenresArray);


        ObjectNode topSongsArray = objectMapper.createObjectNode();

        List<Map.Entry<String, Integer>> topSongs = user.getUsersHistory()
                .getTopSongs(command.getTimestamp());
        for (Map.Entry<String, Integer> entry : topSongs) {
            topSongsArray.put(entry.getKey(), entry.getValue());
        }
        resultObjectNode.set("topSongs", topSongsArray);

        ObjectNode topAlbumsArray = objectMapper.createObjectNode();

        List<Map.Entry<Album, Integer>> topAlbums = user.getUsersHistory()
                .getTopAlbums(command.getTimestamp(), false);
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
}
