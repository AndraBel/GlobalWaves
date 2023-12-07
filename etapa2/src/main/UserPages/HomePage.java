package main.UserPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Playlist;
import main.Song;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends Page {
    public HomePage(ArrayList<Song> likedSongs, ArrayList<Playlist> followingPlaylists) {
        super(likedSongs, followingPlaylists);
    }

    public List<String> getFirst5LikedSongNames() {
        return likedSongs.stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .limit(5)
                .map(Song::getName)
                .collect(Collectors.toList());
    }
    public List<String> getFirst5FollowingPlaylistNames() {
        return followingPlaylists.stream()
                .sorted(Comparator.comparingInt(Playlist::getTotalLikes).reversed())
                .limit(5)
                .map(Playlist::getName)
                .collect(Collectors.toList());
    }
    @Override
    public void getContent(ObjectNode resultNode) {
        List<String> likedSongNames = this.getFirst5LikedSongNames();

        List<String> followingPlaylistNames = this.getFirst5FollowingPlaylistNames();

        resultNode.put("message", "Liked songs:\n\t" + likedSongNames
                + "\n\nFollowed playlists:\n\t"
                + followingPlaylistNames);
    }

    @Override
    public void decreaseListeners() {

    }
}
