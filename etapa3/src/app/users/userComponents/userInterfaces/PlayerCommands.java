package app.users.userComponents.userInterfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.admin.Command;
import app.admin.Library;

public interface PlayerCommands {
    /**
     * This method loads a song, a playlist or a podcast
     *
     * @param command The command to be executed
     * @param library The library to be searched
     * @return ObjectNode with the result of the load command
     */
    ObjectNode load(Command command, Library library);

    /**
     * This method plays or pauses a song, a playlist or a podcast
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the playPause command
     */
    ObjectNode playPause(Command command);

    /**
     * This method repeats what is being played depending on the command
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the repeat command
     */
    ObjectNode repeat(Command command);

    /**
     * This method shuffles or unshuffles what is being played
     * depending on the command
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the shuffle command
     */
    ObjectNode shuffle(Command command);

    /**
     * This method is used only for podcasts and it forwards or
     * backwards the current podcast with 90 seconds depending on the command
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the forwardBackward command
     */
    ObjectNode forwardBackward(Command command);

    /**
     * This method is used only for songs and it likes or unlikes the current
     * song depending on the playing songs status
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the like command
     */
    ObjectNode like(Command command);

    /**
     * This method is playing the next or the previous audio file
     * depending on the command and its status
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the nextPrev command
     */
    ObjectNode nextPrev(Command command);

    /**
     * This method adds or removes the current song to/from the current
     * playlist depending on if it already exists or not
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the addRemoveInPlaylist command
     */
    ObjectNode addRemoveInPlaylist(Command command);

    /**
     * This method shows the status of the player
     *
     * @param command The command to be executed
     * @return ObjectNode with the result of the status command
     */
    ObjectNode status(Command command);
}
