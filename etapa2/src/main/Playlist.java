package main;

import java.util.ArrayList;

/**
 * Class representing a playlist
 */
public class Playlist extends AudioFiles {
    private ArrayList<Song> songs;
    private boolean visibility;
    private int followers;
    private Integer listeners;

    public Playlist(final String name, final String owner) {
        this.name = name;
        this.owner = owner;
        this.songs = new ArrayList<>();
        this.visibility = true;
        followers = 0;
        listeners = 0;
    }

    public void increaseListeners() {
        listeners++;
    }

    public void decreaseListeners() {
        listeners--;
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

    public int getTotalLikes() {
        int totalLikes = 0;
        for (Song song : songs) {
            totalLikes += song.getLikes();
        }
        return totalLikes;
    }

    /**
     * Sets the list of songs associated with the user.
     *
     * @param songs The ArrayList of Song objects to set as the user's songs.
     */
    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
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


    public Integer getListeners() {
        return listeners;
    }
}
