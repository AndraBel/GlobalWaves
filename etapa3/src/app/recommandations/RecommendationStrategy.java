package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.users.artist.Artist;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Map;

public interface RecommendationStrategy {
    /**
     * Generates a random playlist recommendation based on the user's top genres.
     *
     * @param command The Command object containing user and other relevant information.
     * @param users   Map of all users.
     * @param artists Map of all artists.
     * @param hosts   Map of all hosts.
     * @param songs   ArrayList of all available songs.
     * @return ObjectNode Result node with the status of the recommendation generation.
     */
    ObjectNode generateRecommendation(Command command,
                                      Map<String, User> users,
                                      Map<String, Artist> artists,
                                      Map<String, Host> hosts,
                                      ArrayList<Song> songs);
}
