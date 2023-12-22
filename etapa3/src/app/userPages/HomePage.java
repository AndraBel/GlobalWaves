package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.audioCollection.Playlist;
import app.audioFiles.Song;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends Page implements PageAccept {
    private static final int MAXSIZE = 5;
    public HomePage(final ArrayList<Song> likedSongs,
                    final ArrayList<Playlist> followingPlaylists) {
        super(likedSongs, followingPlaylists);
    }

    /**
     * @return the first 5 liked song names
     */
    public List<String> getFirst5LikedSongNames() {
        return likedSongs.stream()
                .sorted(Comparator.comparingInt(Song::getLikes).reversed())
                .limit(MAXSIZE)
                .map(Song::getName)
                .collect(Collectors.toList());
    }

    /**
     * @return the top 5 following playlist names
     */
    public List<String> getFirst5FollowingPlaylistNames() {
        return this.followingPlaylists.stream()
                .sorted(Comparator.comparingInt(Playlist::getTotalLikes).reversed())
                .limit(MAXSIZE)
                .map(Playlist::getName)
                .collect(Collectors.toList());
    }

    /**
     * Accepts a visitor and calls the visit method on it
     */
    @Override
    public void accept(final PageVisitor visitor, final ObjectNode resultNode) {
        visitor.visit(this, resultNode);
    }

    /**
     * Updates the resultNode with the content of the page
     * @param resultNode The node to be updated with the content
     */
    @Override
    public void getContent(final ObjectNode resultNode) {
        List<String> likedSongNames = this.getFirst5LikedSongNames();

        List<String> followingPlaylistNames = this.getFirst5FollowingPlaylistNames();

        resultNode.put("message", "Liked songs:\n\t" + likedSongNames
                + "\n\nFollowed playlists:\n\t"
                + followingPlaylistNames);
    }

    /**
     * Common method for decreasing the number of listeners but not necessary for this page
     */
    @Override
    public void decreaseListeners() {
        // Do nothing
    }
}
