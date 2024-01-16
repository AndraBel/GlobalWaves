package app.users.userComponents.publicity;

import app.users.user.Observer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Notifications implements Observer {
    private ArrayNode resultsArray;
    private ObjectMapper objectMapper;

    public Notifications() {
        objectMapper = new ObjectMapper();
        resultsArray = objectMapper.createArrayNode();
    }

    /**
     * Updates the result array with a new notification.
     * This method creates a new JSON object node and adds it to the results array.
     * The new node contains the notification name and description.
     *
     * @param notification The name or title of the notification.
     * @param description  The description or details of the notification.
     */
    public void update(final String notification, final String description) {
        ObjectNode newNode = objectMapper.createObjectNode();

        newNode.put("name", notification);
        newNode.put("description", description);

        resultsArray.add(newNode);
    }

    /**
     * Retrieves all notifications and resets the results array.
     * This method adds the current results array to the provided result node under 'notifications'.
     *
     * @param resultNode The JSON object node to which the notifications will be added.
     * @return ObjectNode The modified result node containing the notifications.
     */
    public ObjectNode getNotifications(final ObjectNode resultNode) {
        resultNode.set("notifications", resultsArray);

        resultsArray = objectMapper.createArrayNode();

        return resultNode;
    }

}
