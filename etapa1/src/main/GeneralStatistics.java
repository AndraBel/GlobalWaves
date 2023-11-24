package main;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface GeneralStatistics {
    /**
     *  Returns the top 5 songs with the most likes
     *
     * @param command The received command
     * @return ObjectNode containing the top 5 results
     */
    ObjectNode getTop5Songs(Command command);

    /**
     *  Returns the top 5 playlists with the most followers
     *
     * @param command The received command
     * @return ObjectNode containing the top 5 results
     */
    ObjectNode getTop5Playlists(Command command);
}
