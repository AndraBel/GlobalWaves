package app.wrapped;

import app.admin.Command;
import app.audioFiles.podcasts.Episode;
import app.users.Host;
import app.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HostStrategy implements AllUsersStrategy {
    private Map<String, Host> hosts;
    private final ObjectMapper objectMapper;

    public HostStrategy(Map<String, Host> hosts) {
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

    public static List<Map.Entry<Episode, Integer>> getTop5Episodes(LinkedHashMap<Episode, Integer> hostsEpisodes) {
        return hostsEpisodes.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public ObjectNode wrapped(Command command) {
        ObjectNode resultNode = createResultNode(command);

        Host host = hosts.get(command.getUsername());

        ObjectNode resultObjectNode = JsonNodeFactory.instance.objectNode();

        LinkedHashMap<Episode, Integer> hostsEpisodes = new LinkedHashMap<>();

        for (User user : host.getListenersList()) {
            user.getPlayer().calculateStatus(command.getTimestamp());
            LinkedHashMap<Episode, Integer> userEpisodes = new LinkedHashMap<>();

            for (Episode episode : user.getUsersHistory().getListenedEpisodes().keySet()) {
                if (episode.getOwner().equals(host.getName())) {
                    userEpisodes.put(episode, user.getUsersHistory().getListenedEpisodes().get(episode));
                }
            }

            for (Map.Entry<Episode, Integer> entry : userEpisodes.entrySet()) {
                Episode episode = entry.getKey();
                Integer count = entry.getValue();

                // If the episode is already in hostsEpisodes, add the count, otherwise put the new count
                hostsEpisodes.merge(episode, count, Integer::sum);
            }
        }

        List<Map.Entry<Episode, Integer>> top5Episodes = getTop5Episodes(hostsEpisodes);

        ObjectNode resultNodeEpisodes = objectMapper.createObjectNode();

        for (Map.Entry<Episode, Integer> entry : top5Episodes) {
            resultNodeEpisodes.put(entry.getKey().getName(), entry.getValue());
        }

        resultObjectNode.set("topEpisodes", resultNodeEpisodes);
        int listeners = host.getListeners();

        resultObjectNode.set("listeners", JsonNodeFactory.instance.numberNode(listeners));

        resultNode.set("result", resultObjectNode);

        return resultNode;
    }
}
