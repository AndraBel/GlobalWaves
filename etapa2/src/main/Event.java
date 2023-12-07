package main;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event {
    private final String owner;
    private final String name;
    private final String description;
    private final String date;

    public Event(final String owner, final String name, final String description, final String date) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
