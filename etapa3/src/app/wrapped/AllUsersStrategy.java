package app.wrapped;

import app.admin.Command;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AllUsersStrategy {
    /**
     * Executes the command wrapped
     * @param command the command to be executed
     * @return the result of the command
     */
    ObjectNode wrapped(Command command);
}
