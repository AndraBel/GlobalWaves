package app.audioFiles.podcasts;

import app.audioFiles.AudioFiles;

import java.util.ArrayList;

/**
 * Class representing a podcast
 */
public class Podcast extends AudioFiles {
    private final ArrayList<Episode> episodes;

    public Podcast(final String name, final String owner, final ArrayList<Episode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
        listeners = 0;
    }

    /**
     * Retrieves the list of episodes associated with the podcast
     *
     * @return An ArrayList of Episode objects representing the episodes associated with the podcast
     */
    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }
}
