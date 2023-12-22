package app.audioFiles.audioCollection;

import app.audioFiles.Song;
import app.audioFiles.AudioFiles;

import java.util.ArrayList;

public class AudioFilesCollection extends AudioFiles {
    protected ArrayList<Song> songs;

    /**
     * @return the total number of likes of all the songs in the album/playlist
     */
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
     * @return the list of songs associated with the user.
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }
}
