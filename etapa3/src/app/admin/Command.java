package app.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import app.audioFiles.Song;
import app.audioFiles.podcasts.Episode;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Command {
    private String command;
    private String username;
    private Integer timestamp;
    private Integer itemNumber;
    private Integer seed;
    private Integer playlistId;
    private String playlistName;
    private String type;
    private HashMap<String, Object> filters;
    private Integer age;
    private String city;
    private String name;
    private Integer releaseYear;
    private String description;
    private ArrayList<Song> songs;
    private String date;
    private Integer price;
    private String nextPage;
    private ArrayList<Episode> episodes;
    private String recommendationType;

    /**
     * Retrieves the filters associated with the command.
     *
     * @return A HashMap containing filters for the command.
     */
    public HashMap<String, Object> getFilters() {
        return filters;
    }

    /**
     * Sets the filters for the command.
     *
     * @param filters A HashMap containing filters to be set for the command.
     */
    public void setFilters(final HashMap<String, Object> filters) {
        this.filters = filters;
    }

    /**
     * Retrieves the type of the command.
     *
     * @return A String representing the type of the command.
     */
    public String getType() {
        return type;
    }


    /**
     * Retrieves the command string.
     *
     * @return A String representing the command string.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command string.
     *
     * @param command A String representing the command string to be set.
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Retrieves the username associated with the command.
     *
     * @return A String representing the username associated with the command.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the command.
     *
     * @param username A String representing the username to be set for the command.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Retrieves the timestamp associated with the command.
     *
     * @return An Integer representing the timestamp associated with the command.
     */
    public Integer getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for the command.
     *
     * @param timestamp An Integer representing the timestamp to be set for the command.
     */
    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retrieves the item number associated with the command.
     *
     * @return An Integer representing the item number associated with the command.
     */
    public Integer getItemNumber() {
        return itemNumber;
    }

    /**
     * Sets the item number for the command.
     *
     * @param itemNumber An Integer representing the item number to be set for the command.
     */
    public void setItemNumber(final Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * Retrieves the seed associated with the command.
     *
     * @return An Integer representing the seed associated with the command.
     */
    public Integer getSeed() {
        return seed;
    }

    /**
     * Sets the seed for the command.
     *
     * @param seed An Integer representing the seed to be set for the command.
     */
    public void setSeed(final Integer seed) {
        this.seed = seed;
    }

    /**
     * Retrieves the playlist ID associated with the command.
     *
     * @return An Integer representing the playlist ID associated with the command.
     */
    public Integer getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets the playlist ID for the command.
     *
     * @param playlistId An Integer representing the playlist ID to be set for the command.
     */
    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * Retrieves the playlist name associated with the command.
     *
     * @return A String representing the playlist name associated with the command.
     */
    public String getPlaylistName() {
        return playlistName;
    }

    /**
     * Sets the playlist name for the command.
     *
     * @param playlistName A String representing the playlist name to be set for the command.
     */
    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    /**
     * Retrieves the age for a user.
     *
     * @return An Integer representing the age.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Retrieves the city for a user.
     *
     * @return A String representing the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Retrieves the name for an album.
     *
     * @return A String representing the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the release year for an album.
     *
     * @return A Integer representing the release year.
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Retrieves the description for an album.
     *
     * @return A String representing the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the songs for an album.
     *
     * @return An ArrayList representing the songs.
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     * Retrieves the date for an event.
     *
     * @return A String representing the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Retrieves the price for a merch.
     *
     * @return A Integer representing the price.
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Retrieves the next page for a user.
     *
     * @return A String representing the next page.
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * Retrieves the episodes for a season.
     *
     * @return An ArrayList representing the episodes.
     */
    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    /**
     * Retrieves the recommendation type for a user.
     *
     * @return A String representing the recommendation type.
     */
    public String getRecommendationType() {
        return recommendationType;
    }
}
