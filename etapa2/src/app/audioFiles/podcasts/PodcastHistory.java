package app.audioFiles.podcasts;

/**
 * Class that represents the history of a podcast
 * It contains the last episode listened and the second of the episode
 */
public class PodcastHistory {
    private Integer lastEpisode;
    private Integer second;

    public PodcastHistory() {
        lastEpisode = 0;
        second = 0;
    }

    /**
     * Gets the value of the lastEpisode attribute.
     *
     * @return The value of the lastEpisode attribute.
     */
    public Integer getLastEpisode() {
        return lastEpisode;
    }

    /**
     * Sets the value of the lastEpisode attribute.
     *
     * @param lastEpisode The new value to set for the lastEpisode attribute.
     */
    public void setLastEpisode(final Integer lastEpisode) {
        this.lastEpisode = lastEpisode;
    }

    /**
     * Gets the value of the second attribute.
     *
     * @return The value of the second attribute.
     */
    public Integer getSecond() {
        return second;
    }

    /**
     * Sets the value of the second attribute.
     *
     * @param second The new value to set for the second attribute.
     */
    public void setSecond(final Integer second) {
        this.second = second;
    }
}
