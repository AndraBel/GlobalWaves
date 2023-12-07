package main.UserPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Playlist;
import main.Song;

import java.util.ArrayList;
import java.util.List;

public class LikedContentPage extends Page {

    public LikedContentPage(ArrayList<Song> likedSongs, ArrayList<Playlist> followingPlaylists) {
        super(likedSongs, followingPlaylists);
    }
    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }
    public ArrayList<Playlist> getFollowingPlaylists() {
        return followingPlaylists;
    }
    @Override
    public void getContent(ObjectNode resultNode) {
//        List<Song> likedSongs = this.getLikedSongs();
        List<String> likedSongDetails = new ArrayList<>();

        // Iterate through liked songs and get their names and artists
        for (Song song : likedSongs) {
            likedSongDetails.add(String.format("%s - %s", song.getName(), song.getArtist()));
        }

        // Assuming followingPlaylists is a list of playlists
        List<String> followingPlaylistDetails = new ArrayList<>();
        for (Playlist playlist : getFollowingPlaylists()) {
            followingPlaylistDetails.add(String.format("%s - %s", playlist.getName(),
                    playlist.getOwner()));
        }

        resultNode.put("message",
                String.format("Liked songs:\n\t[%s]\n\nFollowed playlists:\n\t[%s]",
                String.join(", ", likedSongDetails), String.join(", ",
                followingPlaylistDetails)));
    }

    @Override
    public void decreaseListeners() {

    }
}
