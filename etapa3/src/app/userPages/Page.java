package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.audioCollection.Playlist;
import app.audioFiles.Song;

import java.util.ArrayList;

public abstract class Page {
    protected ArrayList<Song> likedSongs;
    protected ArrayList<Playlist> followingPlaylists;

    public Page(final ArrayList<Song> likedSongs,
                final ArrayList<Playlist> followingPlaylists) {
        this.likedSongs = likedSongs;
        this.followingPlaylists = followingPlaylists;
    }

    /**
     * Common method for accepting a visitor
     *
     * @param visitor The visitor to be accepted
     */
    public abstract void accept(PageVisitor visitor, ObjectNode resultNode);

    /**
     * Common method for getting the content of a page
     *
     * @param resultNode The node to be updated with the content
     */
    public abstract void getContent(ObjectNode resultNode);

    /**
     * Common method for decreasing the number of listeners
     */
    public abstract void decreaseListeners();

    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    public ArrayList<Playlist> getFollowingPlaylists() {
        return followingPlaylists;
    }
}
