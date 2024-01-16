package app.wrapped;

import app.admin.Command;
import app.audioFiles.Song;
import app.audioFiles.audioCollection.Album;
import app.users.artist.Artist;
import app.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArtistStrategy implements AllUsersStrategy {
    private Map<String, Artist> artists;
    private final ObjectMapper objectMapper;
    private LinkedHashMap<String, User> users;

    private static final int TOP_FIVE = 5;

    private ObjectNode createResultNode(final Command command) {
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", command.getCommand());
        resultNode.put("user", command.getUsername());
        resultNode.put("timestamp", command.getTimestamp());
        return resultNode;
    }

    public ArtistStrategy(final Map<String, Artist> artists,
                          final LinkedHashMap<String, User> users) {
        this.artists = artists;
        objectMapper = new ObjectMapper();
        this.users = users;
    }

    /***
     * Method for getting the top 5 fans of an artist
     * @param artistName
     * @param timestamp
     * @return a LinkedHashMap containing the top 5 fans
     */
    public LinkedHashMap<User, Integer> getTop5Fans(final String artistName,
                                                    final Integer timestamp) {
        LinkedHashMap<User, Integer> topFans = new LinkedHashMap<>();

        for (Map.Entry<String, User> entry : users.entrySet()) {
            entry.getValue().getPlayer().calculateStatus(timestamp);
            for (Map.Entry<String, Integer> artist : entry.getValue().getUsersHistory()
                    .getListenedArtists().entrySet()) {
                if (artist.getKey().equals(artistName)) {
                    topFans.put(entry.getValue(), artist.getValue());
                }
            }
        }

        LinkedHashMap<User, Integer> sortedTopFans = topFans.entrySet().stream()
                .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
                .limit(TOP_FIVE)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        return sortedTopFans;
    }

    private static List<Map.Entry<String, Integer>>
    getTop5SongsList(final LinkedHashMap<String, Integer> artistsSongs) {
        return artistsSongs.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int valueComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (valueComparison == 0) {
                        return entry1.getKey().compareTo(entry2.getKey());
                    }
                    return valueComparison;
                })
                .limit(TOP_FIVE)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Album, Integer>>
    getTop5AlbumsList(final LinkedHashMap<Album, Integer> artistsAlbums) {
        return artistsAlbums.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    int valueComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (valueComparison == 0) {
                        return entry1.getKey().getName().compareTo(entry2.getKey().getName());
                    }
                    return valueComparison;
                })
                .limit(TOP_FIVE)
                .collect(Collectors.toList());
    }

    private LinkedHashMap<String, Integer> getTopSongs(final Artist artist,
                                                       final Integer timestamp) {
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

                /* If the song is already in artistsSongs,
                   add the count, otherwise put the new count */
                artistsSongs.merge(song.getName(), count, Integer::sum);
            }
        }
        return artistsSongs;
    }

    /**
     * Method for creating a wrapped result for an artist
     *
     * @param command the command that is given
     * @return an ObjectNode containing the result
     */
    @Override
    public ObjectNode wrapped(final Command command) {
        ObjectNode resultNode = createResultNode(command);

        Artist artist = artists.get(command.getUsername());

        ObjectNode resultObjectNode = JsonNodeFactory.instance.objectNode();

        LinkedHashMap<Album, Integer> artistsAlbums = new LinkedHashMap<>();

        for (User user : artist.getListenersList()) {
            user.getPlayer().calculateStatus(command.getTimestamp());
            LinkedHashMap<Album, Integer> userAlbums = new LinkedHashMap<>();

            for (Album album : user.getUsersHistory().
                    getListenedAlbums(true).keySet()) {
                if (album.getArtist().equals(artist.getName())) {
                    userAlbums.put(album, user.getUsersHistory()
                            .getListenedAlbums(true).get(album));
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

        LinkedHashMap<User, Integer> topFans = getTop5Fans(artist.getName(),
                command.getTimestamp());
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
            resultNode.put("message", "No data to show for artist "
                    + command.getUsername() + ".");
            return resultNode;
        }

        return resultNode;
    }
}
