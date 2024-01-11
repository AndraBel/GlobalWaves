package app.users.userComponents.publicity;

import app.users.user.Observer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Notifications implements Observer {
    ArrayNode resultsArray;
    ObjectMapper objectMapper;

    public Notifications() {
        objectMapper = new ObjectMapper();
        resultsArray = objectMapper.createArrayNode();
    }

    public void update(String notification, String description) {
        ObjectNode newNode = objectMapper.createObjectNode();

        newNode.put("name", notification);
        newNode.put("description", description);

        resultsArray.add(newNode);
    }

    public ObjectNode getNotifications(ObjectNode resultNode) {
        resultNode.set("notifications", resultsArray);

        resultsArray = objectMapper.createArrayNode();

        return resultNode;
    }
}
