package main;

/**
 * Class that represents an episode of a podcast
 * It contains the name, duration and description of the episode
 */
public class Episode {
    private final String name;
    private final Integer duration;
    private final String description;

    public Episode(final String name, final Integer duration, final String description) {
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
}
