package app.recommandations;

import app.admin.Command;
import app.audioFiles.Song;
import app.users.arist.Artist;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Map;

public class RecommendationContext {
    private RecommendationStrategy strategy;

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public ObjectNode executeStrategy(Command command, Map<String, User> users, Map<String, Artist> artists,
                                      Map<String, Host> hosts, ArrayList<Song> songs) {
        return strategy.generateRecommendation(command, users, artists, hosts, songs);
    }
}
