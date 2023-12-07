package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that represents an episode of a podcast
 * It contains the name, duration and description of the episode
 */
public class Episode {
    private final String name;
    private final Integer duration;
    private final String description;

    @JsonCreator
    public Episode(@JsonProperty("name") String name,
                   @JsonProperty("duration") Integer duration,
                   @JsonProperty("description") String description) {
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    /**
     *
     * @return the name of the episode
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return the duration of the episode
     */
    public Integer getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }
}
