package app.users.userComponents.userInterfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.admin.Command;

public interface PlaylistCommands {
    /**
     * This method creates a playlist
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the createPlaylist command
     */
    ObjectNode createPlaylist(Command command);

    /**
     * This method changes the visibility of a playlist
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the switchVisibility command
     */
    ObjectNode switchVisibility(Command command);

    /**
     * This method follows a playlist
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the followPlaylist command
     */
    ObjectNode followPlaylist(Command command);

    /**
     * This method shows all playlists with all of its songs
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the showPlaylists command
     */
    ObjectNode showPlaylists(Command command);
}
