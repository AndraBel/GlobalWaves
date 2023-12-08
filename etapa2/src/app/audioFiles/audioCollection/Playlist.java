package app.audioFiles.audioCollection;

import app.audioFiles.Song;

import java.util.ArrayList;

/**
 * Class representing a playlist
 */
public class Playlist extends AudioFilesCollection {
    private boolean visibility;
    private int followers;

    public Playlist(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
        this.songs = new ArrayList<>();
        this.visibility = true;
        followers = 0;
        listeners = 0;
    }

    /**
     * Increases the follower count by one, representing a user following action.
     */
    public void follow() {
        followers++;
    }

    /**
     * Decreases the follower count by one, representing a user unfollowing action.
     */
    public void unfollow() {
        followers--;
    }

    /**
     * Sets the visibility status of the user's content.
     *
     * @param visibility The boolean value indicating the visibility status
     *                   (true for visible, false for hidden).
     */
    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Retrieves the list of songs associated with the user.
     *
     * @return An ArrayList of Song objects representing the songs associated with the user.
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     * Retrieves the visibility status of the user's content.
     *
     * @return A boolean value indicating the visibility status
     * (true for visible, false for hidden).
     */
    public boolean isVisibility() {
        return visibility;
    }

    /**
     * Retrieves the count of followers for the user.
     *
     * @return An integer representing the number of followers the user has.
     */
    public int getFollowers() {
        return followers;
    }
}
