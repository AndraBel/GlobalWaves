package main.UserPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Playlist;
import main.Song;

import java.util.ArrayList;

public abstract class Page {
    public ArrayList<Song> likedSongs;
    public ArrayList<Playlist> followingPlaylists;

    public Page (final ArrayList<Song> likedSongs,
                 final ArrayList<Playlist> followingPlaylists) {
        this.likedSongs = likedSongs;
        this.followingPlaylists = followingPlaylists;
    }

    public abstract void getContent(ObjectNode resultNode);

    public abstract void decreaseListeners();
}
