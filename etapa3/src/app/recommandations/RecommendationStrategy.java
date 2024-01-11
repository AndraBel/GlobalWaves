package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.users.arist.Artist;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Map;

public interface RecommendationStrategy {
    ObjectNode generateRecommendation(Command command, Map<String, User> users, Map<String, Artist> artists,
                                      Map<String, Host> hosts, ArrayList<Song> songs);
}
