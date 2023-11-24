package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Player {
    private Song currentSong;
    private Playlist currentPlaylist;
    private ArrayList<Song> unsuffledSongs;
    private Podcast currentPodcast;
    private Integer songIndex;
    private Integer lastCommandTimestamp;
    private Integer playTime;
    private String playMode;
    private final HashMap<String, PodcastHistory> podcastsHistory;
    private String repeat;
    private boolean shuffle;
    private boolean paused;
    private final ArrayList<Song> likedSongs;
    private static final int SECONDS = 90;

    public Player() {
        podcastsHistory = new HashMap<>();
        likedSongs = new ArrayList<>();
        repeat = "no repeat";
        shuffle = false;
        paused = false;
        playMode = "clear";
    }

    /**
     * This method resets the player
     *
     * @param loadTimestamp The timestamp of the load command
     */
    public void resetPlayer(final Integer loadTimestamp) {
        calculateStatus(loadTimestamp);
        if (playMode.equals("podcast")) {
            PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
            history.setSecond(playTime);
        }
        this.playMode = "clear";
        playTime = 0;
        paused = true;
        repeat = "no repeat";
        lastCommandTimestamp = loadTimestamp;
    }

    /**
     * Resets the player state to its initial conditions
     */
    private void resetLoad() {
        playTime = 0;
        paused = false;
        repeat = "no repeat";
        if (shuffle) {
            unsuffle();
        }
        shuffle = false;
    }

    /**
     * Loads a song for player and initializes player parameters.
     * @param song The Song object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Song song, final Integer loadTimestamp) {
        calculateStatus(loadTimestamp);
        currentSong = song;
        lastCommandTimestamp = loadTimestamp;
        playMode = "song";
        resetLoad();
    }

    /**
     * Loads a playlist and initializes player parameters.
     * @param playlist The Playlist object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Playlist playlist, final Integer loadTimestamp) {
        if (playlist.getSongs().isEmpty()) {
            return;
        }
        calculateStatus(loadTimestamp);
        currentSong = playlist.getSongs().get(0);
        songIndex = 0;
        currentPlaylist = playlist;
        lastCommandTimestamp = loadTimestamp;
        playMode = "playlist";
        resetLoad();
    }

    /**
     * Loads a podcast and initializes player parameters.
     * @param podcast The Podcast object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Podcast podcast, final Integer loadTimestamp) {
        calculateStatus(loadTimestamp);
        if (!podcastsHistory.containsKey(podcast.getName())) {
            podcastsHistory.put(podcast.getName(), new PodcastHistory());
        }
        currentPodcast = podcast;
        lastCommandTimestamp = loadTimestamp;
        playMode = "podcast";
        resetLoad();
    }

    /**
     * Calculates the player status for a song based on the current repeat mode.
     */
    private void calculateStatusSong() {
        switch (repeat) {
            case ("no repeat"):
                if (playTime > currentSong.getDuration()) {
                    playMode = "clear";
                    playTime = 0;
                    paused = true;
                }
                break;
            case ("repeat once"):
                if (playTime > currentSong.getDuration()) {
                    playTime -= currentSong.getDuration();
                    repeat = "no repeat";

                    if (playTime > currentSong.getDuration()) {
                        playMode = "clear";
                        playTime = 0;
                        paused = true;
                    }
                }
                break;
            case ("repeat infinite"):
                playTime = playTime - (playTime / currentSong.getDuration())
                        * currentSong.getDuration();
                break;
            default:
                break;
        }
    }

    /**
     * Calculates the player status for a playlist based on the current repeat mode.
     */
    private void calculateStatusPlaylist() {
        switch (repeat) {
            case ("no repeat"):
                while (playTime > currentPlaylist.getSongs().get(songIndex).getDuration()) {
                    // If is the playlist's last song, the player is reset
                    if (songIndex == currentPlaylist.getSongs().size() - 1) {
                        playMode = "clear";
                        playTime = 0;
                        paused = true;
                        if (shuffle) {
                            unsuffle();
                        }
                        shuffle = false;
                        break;
                    } else {
                        playTime -= currentPlaylist.getSongs().get(songIndex).getDuration();
                        songIndex += 1;
                    }
                }
                break;
            case ("repeat all"):
                // In this case the player does not reset, it always plays the next song
                while (playTime > currentPlaylist.getSongs().get(songIndex).getDuration()) {
                    playTime -= currentPlaylist.getSongs().get(songIndex).getDuration();
                    songIndex = (songIndex + 1) % currentPlaylist.getSongs().size();
                }
                break;
            case ("repeat current song"):
                // In this case the player does not reset, it always plays the same song
                playTime = playTime
                        - (playTime / currentPlaylist.getSongs().get(songIndex).getDuration())
                        * currentPlaylist.getSongs().get(songIndex).getDuration();
                break;
            default:
                break;
        }
    }

    /**
     * Updates the status of a podcast based on the current repeat mode.
     * @param history The PodcastHistory object for the current podcast.
     */
    private void updateStatusPodcast(final PodcastHistory history) {
        while (playTime > currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration()
                - history.getSecond()) {
            if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                history.setLastEpisode(0);
                history.setSecond(0);
                playMode = "clear";
                playTime = 0;
                paused = true;
                break;
            } else {
                int duration = currentPodcast.getEpisodes().get(history.getLastEpisode())
                        .getDuration();
                playTime = playTime - (duration - history.getSecond());
                history.setLastEpisode(history.getLastEpisode() + 1);
                history.setSecond(0);
            }
        }
    }

    /**
     * Calculates the player status for a podcast based on the current repeat mode.
     */
    private void calculateStatusPodcast() {
        // Retrieve the podcast history for the current podcast.
        PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
        int duration = currentPodcast.getEpisodes().get(history.getLastEpisode())
                .getDuration();
        switch (repeat) {
            // If repeat mode is "no repeat", update the podcast status
            case ("no repeat"):
                updateStatusPodcast(history);
                break;
            case ("repeat once"):
                while (playTime > duration - history.getSecond()) {
                    // If it is the last episode, change the repeat mode to "no repeat" and start
                    // playing the first episode
                    if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                        history.setLastEpisode(0);
                        history.setSecond(0);
                        repeat = "no repeat";
                        duration = currentPodcast.getEpisodes().get(history.getLastEpisode())
                                .getDuration();
                        playTime = playTime - duration;

                        updateStatusPodcast(history);
                        break;
                    } else {
                        playTime = playTime - (duration - history.getSecond());
                        history.setLastEpisode(history.getLastEpisode() + 1);
                        history.setSecond(0);
                    }
                    duration = currentPodcast.getEpisodes().get(history.getLastEpisode())
                            .getDuration();
                }
                break;
            case ("repeat infinite"):
                // Repeats the podcast infinitely and calculates the play time
                duration = currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration();
                while (playTime > duration - history.getSecond()) {
                    playTime = playTime - (duration - history.getSecond());
                    history.setLastEpisode((history.getLastEpisode() + 1)
                            % currentPodcast.getEpisodes().size());
                    history.setSecond(0);
                    duration = currentPodcast.getEpisodes().get(history.getLastEpisode())
                            .getDuration();
                }
                break;
            default:
                break;
        }
    }


    /**
     * Calculates the player status based on the current play mode.
     * @param commandTimestamp The timestamp of the command.
     */
    private void calculateStatus(final Integer commandTimestamp) {
        if (!paused && !playMode.equals("clear")) {
            playTime += commandTimestamp - lastCommandTimestamp;
        }

        switch (playMode) {
            case ("clear"):
                break;
            case ("song"):
                calculateStatusSong();
                break;
            case ("playlist"):
                calculateStatusPlaylist();
                break;
            case ("podcast"):
                calculateStatusPodcast();
                break;
            default:
                break;
        }
    }

    /**
     * Returns the player status as a JSON object.
     * @param commandTimestamp The timestamp of the command.
     * @return The player status as a JSON object.
     */
    public ObjectNode status(final Integer commandTimestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode newNode = objectMapper.createObjectNode();
        calculateStatus(commandTimestamp);

        switch (playMode) {
            case ("song"):
                newNode.put("name", currentSong.getName());
                newNode.put("remainedTime", currentSong.getDuration() - playTime);
                break;
            case ("playlist"):
                newNode.put("name", currentPlaylist.getSongs().get(songIndex).getName());
                newNode.put("remainedTime",
                        currentPlaylist.getSongs().get(songIndex).getDuration() - playTime);
                break;
            case ("podcast"):
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                newNode.put("name",
                        currentPodcast.getEpisodes().get(history.getLastEpisode()).getName());
                newNode.put("remainedTime",
                        currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration()
                                - playTime - history.getSecond());
                break;
            case ("clear"):
                newNode.put("name", "");
                newNode.put("remainedTime", playTime);
                break;
            default:
                break;
        }
        switch (repeat) {
            case "no repeat" -> newNode.put("repeat", "No Repeat");
            case "repeat all" -> newNode.put("repeat", "Repeat All");
            case "repeat current song" -> newNode.put("repeat", "Repeat Current Song");
            case "repeat once" -> newNode.put("repeat", "Repeat Once");
            case "repeat infinite" -> newNode.put("repeat", "Repeat Infinite");
            default -> throw new IllegalStateException("Unexpected value: " + repeat);
        }

        newNode.put("shuffle", shuffle);
        newNode.put("paused", paused);
        lastCommandTimestamp = commandTimestamp;

        return newNode;
    }

    /**
     * Plays or pauses the player.
     * @param commandTimestamp The timestamp of the command.
     */
    public void playPause(final Integer commandTimestamp) {
        calculateStatus(commandTimestamp);
        paused = !paused;
        lastCommandTimestamp = commandTimestamp;
    }

    /**
     * Changes the repeat mode.
     * @param commandTimestamp The timestamp of the command.
     * @return The new repeat mode.
     */
    public String repeat(final Integer commandTimestamp) {
        calculateStatus(commandTimestamp);

        if (repeat.equals("repeat current song") || repeat.equals("repeat infinite")) {
            repeat = "no repeat";
        } else if (playMode.equals("playlist") && repeat.equals("no repeat")) {
            repeat = "repeat all";
        } else if ((playMode.equals("song") || playMode.equals("podcast"))
                && repeat.equals("no repeat")) {
            repeat = "repeat once";
        } else if (playMode.equals("playlist") && repeat.equals("repeat all")) {
            repeat = "repeat current song";
        } else if ((playMode.equals("song") || playMode.equals("podcast"))
                && repeat.equals("repeat once")) {
            repeat = "repeat infinite";
        }

        lastCommandTimestamp = commandTimestamp;
        return repeat;
    }

    /**
     * Likes or unlikes a song.
     * @param command The command object.
     * @return True if the song is liked, false otherwise.
     */
    public boolean like(final Command command) {
        calculateStatus(command.getTimestamp());
        lastCommandTimestamp = command.getTimestamp();

        Song song;
        if (playMode.equals("song")) {
            song = currentSong;
        } else {
            song = currentPlaylist.getSongs().get(songIndex);
        }

        if (likedSongs.contains(song)) {
            song.unlikeSong();
            likedSongs.remove(song);
            return false;
        }

        likedSongs.add(song);
        song.likeSong();
        return true;
    }

    private void nextSong(final ObjectNode resultNode) {
        switch (repeat) {
            case ("no repeat"):
                playMode = "clear";
                paused = true;
                resultNode.put("message",
                        "Please load a source before skipping to the next track.");
                break;
            case ("repeat once"):
                repeat = "no repeat";
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentSong.getName() + ".");
                paused = false;
                break;
            case ("repeat infinite"):
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentSong.getName() + ".");
                paused = false;
                break;
            default:
                break;

        }
    }

    private void nextPlaylist(final ObjectNode resultNode) {
        switch (repeat) {
            case ("no repeat"):
                if (songIndex == currentPlaylist.getSongs().size() - 1) {
                    playMode = "clear";
                    paused = true;
                    if (shuffle) {
                        unsuffle();
                    }
                    shuffle = false;
                    resultNode.put("message",
                            "Please load a source before skipping to the next track.");
                } else {
                    songIndex++;
                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                    paused = false;
                }
                break;
            case ("repeat all"):
                songIndex = (songIndex + 1) % currentPlaylist.getSongs().size();
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                paused = false;
                break;
            case ("repeat current song"):
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                paused = false;
                break;
            default:
                break;
        }

    }

    private void nextPodcast(final ObjectNode resultNode) {
        PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
        switch (repeat) {
            case ("no repeat"):
                if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                    playMode = "clear";
                    paused = true;
                    history.setSecond(0);
                    history.setLastEpisode(0);
                    resultNode.put("message",
                            "Please load a source before skipping to the next track.");
                } else {
                    history.setSecond(0);
                    history.setLastEpisode(history.getLastEpisode() + 1);
                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentPodcast.getEpisodes().get(history.getLastEpisode())
                                    .getName() + ".");
                    paused = false;
                }
                break;
            case ("repeat once"):
                if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                    repeat = "no repeat";
                    history.setLastEpisode(0);
                    history.setSecond(0);
                } else {
                    history.setSecond(0);
                    history.setLastEpisode(history.getLastEpisode() + 1);
                }
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentPodcast.getEpisodes().get(history.getLastEpisode())
                                .getName() + ".");
                paused = false;
                break;
            case ("repeat infinite"):
                history.setLastEpisode((history.getLastEpisode() + 1)
                        % currentPodcast.getEpisodes().size());
                history.setSecond(0);
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentPodcast.getEpisodes().get(history.getLastEpisode())
                                .getName() + ".");
                paused = false;
                break;
            default:
                break;
        }
    }

    /**
     * Skips to the next track depending on the current repeat mode.
     * @param  command The given command
     * @param resultNode The JSON object
     */
    public void next(final Command command, final ObjectNode resultNode) {
        calculateStatus(command.getTimestamp());
        switch (playMode) {
            case "song":
                nextSong(resultNode);
                break;
            case "playlist":
                nextPlaylist(resultNode);
                break;
            case "podcast":
                nextPodcast(resultNode);
                break;
            default:
                break;
        }
        playTime = 0;
        lastCommandTimestamp = command.getTimestamp();
    }

    /**
     * Skips to the previous track depending on the current player status.
     * @param command The given command
     * @param resultNode The JSON object
     */
    public void prev(final Command command, final ObjectNode resultNode) {
        calculateStatus(command.getTimestamp());
        switch (playMode) {
            case "song":
                resultNode.put("message",
                        "Returned to previous track successfully. The current track is "
                                + currentSong.getName() + ".");
                break;
            case "playlist":
                if (songIndex != 0 && playTime == 0 && !paused) {
                    songIndex--;
                }
                resultNode.put("message",
                        "Returned to previous track successfully. The current track is "
                                + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                break;
            case "podcast":
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                if (history.getLastEpisode() != 0 && history.getSecond() + playTime == 0) {
                    history.setLastEpisode(history.getLastEpisode() - 1);
                }
                history.setSecond(0);
                resultNode.put("message",
                        "Returned to previous track successfully. The current track is "
                                + currentPodcast.getEpisodes().get(history.getLastEpisode())
                                .getName() + ".");
                break;
            default:
                break;
        }
        playTime = 0;
        paused = false;
        lastCommandTimestamp = command.getTimestamp();
    }

    /**
     * Skips forward or backward ninety seconds depending on the current player status.
     * @param command The given command
     */
    public void forwardBackward(final Command command) {
        calculateStatus(command.getTimestamp());
        PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
        if (command.getCommand().equals("forward")) {
            int remainedTime = currentPodcast.getEpisodes()
                    .get(history.getLastEpisode()).getDuration() - playTime - history.getSecond();
            if (remainedTime <= SECONDS && history.getLastEpisode()
                    != currentPodcast.getEpisodes().size() - 1) {
                history.setLastEpisode(history.getLastEpisode() + 1);
                history.setSecond(0);
                playTime = 0;
            } else if (remainedTime > SECONDS && history.getLastEpisode()
                    != currentPodcast.getEpisodes().size() - 1) {
                playTime += SECONDS;
            } else {
                history.setLastEpisode(0);
                history.setSecond(0);
                playTime = 0;
            }
        } else {
            int currentTime = playTime + history.getSecond();
            if (currentTime <= SECONDS) {
                history.setSecond(0);
                playTime = 0;
            } else {
                playTime -= SECONDS;
            }
        }

        lastCommandTimestamp = command.getTimestamp();
    }

    /**
     * Unsuflles the playlist depending on the current player status.
     */
    private void unsuffle() {
        songIndex = unsuffledSongs.indexOf(currentPlaylist.getSongs().get(songIndex));
        currentPlaylist.setSongs(unsuffledSongs);
    }

    /**
     * Activates or deactivates the shuffle function depending on the current player status.
     * @param command The given command
     * @param resultNode The JSON object
     */
    public void shuffle(final Command command, final ObjectNode resultNode) {
        calculateStatus(command.getTimestamp());

        if (playMode.equals("clear")) {
            resultNode.put("message",
                    "Please load a source before using the shuffle function.");
            return;
        }

        if (!playMode.equals("playlist")) {
            resultNode.put("message",
                    "The loaded source is not a playlist.");
            return;
        }

        if (shuffle) {
            unsuffle();
            shuffle = false;
            resultNode.put("message",
                    "Shuffle function deactivated successfully.");
        } else {
            unsuffledSongs = new ArrayList<>(currentPlaylist.getSongs());
            ArrayList<Song> shuffledSongs = new ArrayList<>(currentPlaylist.getSongs());
            Collections.shuffle(shuffledSongs, new Random(command.getSeed()));
            songIndex = shuffledSongs.indexOf(unsuffledSongs.get(songIndex));
            currentPlaylist.setSongs(shuffledSongs);
            shuffle = true;
            resultNode.put("message", "Shuffle function activated successfully.");
        }
        lastCommandTimestamp = command.getTimestamp();
    }

    /**
     * Returns the current song.
     * @return The current song.
     */
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Returns the current playlist.
     * @return The current playlist.
     */
    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * @return The current player mode.
     */
    public String getPlayMode() {
        return playMode;
    }

    /**
     * @return The current repeat mode.
     */
    public String getRepeat() {
        return repeat;
    }

    /**
     * @return if the playlist is shuffled or not.
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * @return if the player is paused or not.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the player to paused or not.
     * @param paused the current state of the player.
     */
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

    /**
     * @return The array of liked songs.
     */
    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    /**
     * @return Returns the unsuffled playlist.
     */
    public ArrayList<Song> getUnsuffledSongs() {
        return unsuffledSongs;
    }
}
