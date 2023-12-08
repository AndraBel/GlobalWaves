package app.users.userComponents.userInterfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.admin.Command;
import app.admin.Library;

public interface SearchBarCommands {
    /**
     * This method searches for audio files that match the filters
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the search command
     */
    ObjectNode search(Command command, Library library);

    /**
     * This method selects audio files that match the filters
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the select command
     */
    ObjectNode select(Command command, Library library);
}
