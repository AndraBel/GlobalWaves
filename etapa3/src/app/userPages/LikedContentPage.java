package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.audioCollection.Playlist;
import app.audioFiles.Song;

import java.util.ArrayList;
import java.util.List;

public class LikedContentPage extends Page implements PageAccept {

    public LikedContentPage(final ArrayList<Song> likedSongs,
                            final ArrayList<Playlist> followingPlaylists) {
        super(likedSongs, followingPlaylists);
    }

    /**
     * @return the liked songs
     */
    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
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
        List<String> likedSongDetails = new ArrayList<>();

        // Iterate through liked songs and get their names and artists
        for (Song song : likedSongs) {
            likedSongDetails.add(String.format("%s - %s", song.getName(), song.getArtist()));
        }

        // Assuming followingPlaylists is a list of playlists
        List<String> followingPlaylistDetails = new ArrayList<>();
        for (Playlist playlist : followingPlaylists) {
            followingPlaylistDetails.add(String.format("%s - %s", playlist.getName(),
                    playlist.getOwner()));
        }

        resultNode.put("message",
                String.format("Liked songs:\n\t[%s]\n\nFollowed playlists:\n\t[%s]",
                String.join(", ", likedSongDetails), String.join(", ",
                followingPlaylistDetails)));
    }

    /**
     * Common method for decreasing the number of listeners but not necessary for this page
     */
    @Override
    public void decreaseListeners() {
        // Do nothing
    }
}
