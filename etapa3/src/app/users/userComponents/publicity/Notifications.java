package app.users.userComponents.publicity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Notifications {
    ArrayNode resultsArray;
    ObjectMapper objectMapper;

    public Notifications() {
        objectMapper = new ObjectMapper();
        resultsArray = objectMapper.createArrayNode();
    }

    public void addNotification(String notification, String description) {
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
