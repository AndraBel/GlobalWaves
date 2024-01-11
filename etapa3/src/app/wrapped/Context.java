package app.wrapped;

import app.admin.Command;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Context {
    private AllUsersStrategy strategy;

    public Context(AllUsersStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(UserStrategy strategy) {
        this.strategy = strategy;
    }

    public ObjectNode wrapped(final Command command) {
        return strategy.wrapped(command);
    }
}
