package app.wrapped;

import app.admin.Command;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AllUsersStrategy {
    ObjectNode wrapped(Command command);
}
