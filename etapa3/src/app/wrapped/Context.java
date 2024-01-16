package app.wrapped;

import app.admin.Command;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Context {
    private AllUsersStrategy strategy;

    public Context(final AllUsersStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Executes the command wrapped for the host
     * @param command the command to be executed
     * @return the result of the command
     */
    public ObjectNode wrapped(final Command command) {
        return strategy.wrapped(command);
    }
}
