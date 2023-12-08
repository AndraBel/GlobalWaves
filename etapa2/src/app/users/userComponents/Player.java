package app.users.userComponents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.audioCollection.Album;
import app.audioFiles.audioCollection.AudioFilesCollection;
import app.audioFiles.audioCollection.Playlist;
import app.audioFiles.Song;
import app.audioFiles.podcasts.Podcast;
import app.audioFiles.podcasts.PodcastHistory;
import app.admin.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Player {
    private Song currentSong;

    private Playlist originalPlaylist;
    private Playlist currentPlaylist;
    private ArrayList<Song> unsuffledSongsPlaylist;
    private ArrayList<Song> unsuffledSongsAlbum;
    private Podcast currentPodcast;
    private Integer songIndex;
    private Integer songIndexAlbum;
    private Integer lastCommandTimestamp;
    private Integer playTime;
    private String playMode;
    private final HashMap<String, PodcastHistory> podcastsHistory;
    private String repeat;
    private boolean shuffleAlbum;
    private boolean shufflePLaylist;
    private boolean paused;
    private final ArrayList<Song> likedSongs;
    private static final int SECONDS = 90;
    private String userStatus;
    private Album currentAlbum;
    private Album originalAlbum;

    public Player() {
        podcastsHistory = new HashMap<>();
        likedSongs = new ArrayList<>();
        repeat = "no repeat";
        shufflePLaylist = false;
        shuffleAlbum = false;
        paused = false;
        playMode = "clear";
        userStatus = "online";
        lastCommandTimestamp = 0;
    }

    /**
     * This method resets the player
     *
     * @param loadTimestamp The timestamp of the load command
     */
    public void resetPlayer(final Integer loadTimestamp) {
        calculateStatus(loadTimestamp);

        switch (playMode) {
            case "album" -> {
                originalAlbum.getSongs().get(songIndexAlbum).decreaseListeners();
                originalAlbum.decreaseListeners();
            }
            case "playlist" -> {
                originalPlaylist.getSongs().get(songIndex).decreaseListeners();
                originalPlaylist.decreaseListeners();
            }
            case "podcast" -> {
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                history.setSecond(playTime);
                currentPodcast.decreaseListeners();
            }
            case "song" -> currentSong.decreaseListeners();
            default -> {
            }
        }

        this.playMode = "clear";
        playTime = 0;
        paused = true;
        repeat = "no repeat";
    }

    /**
     * Resets the player state to its initial conditions
     */
    private void resetLoad() {
        switch (playMode) {
            case "album" -> {
                originalAlbum.getSongs().get(songIndexAlbum).decreaseListeners();
                originalAlbum.decreaseListeners();
            }
            case "playlist" -> {
                originalPlaylist.getSongs().get(songIndex).decreaseListeners();
                originalPlaylist.decreaseListeners();
            }
            case "song" -> currentSong.decreaseListeners();
            default -> {
            }
        }

        playTime = 0;
        paused = false;
        repeat = "no repeat";
        if (shufflePLaylist || shuffleAlbum) {
            unsuffle();
        }
        shufflePLaylist = false;
        shuffleAlbum = false;
    }

    /**
     * Loads a song for player and initializes player parameters.
     *
     * @param song          The Song object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Song song, final Integer loadTimestamp) {
        resetLoad();
        calculateStatus(loadTimestamp);
        currentSong = song;
        playMode = "song";
    }

    /**
     * Loads a playlist and initializes player parameters.
     *
     * @param playlist      The Playlist object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Playlist playlist, final Integer loadTimestamp) {
        if (playlist.getSongs().isEmpty()) {
            return;
        }
        resetLoad();
        calculateStatus(loadTimestamp);
        currentSong = playlist.getSongs().get(0);
        currentSong.increaseListeners();
        songIndex = 0;
        originalPlaylist = playlist;
        currentPlaylist = new Playlist(playlist.getName(), playlist.getOwner());
        currentPlaylist.setSongs(playlist.getSongs());
        playMode = "playlist";
    }

    /**
     * Loads a podcast and initializes player parameters.
     *
     * @param podcast       The Podcast object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Podcast podcast, final Integer loadTimestamp) {
        resetLoad();
        calculateStatus(loadTimestamp);
        if (!podcastsHistory.containsKey(podcast.getName())) {
            podcastsHistory.put(podcast.getName(), new PodcastHistory());
        }
        currentPodcast = podcast;
        playMode = "podcast";
    }

    /**
     * Loads an album and initializes player parameters.
     *
     * @param album         The Album object to be loaded.
     * @param loadTimestamp The timestamp at which the load command is executed.
     */
    public void load(final Album album, final Integer loadTimestamp) {
        resetLoad();
        calculateStatus(loadTimestamp);
        currentSong = album.getSongs().get(0);
        currentSong.increaseListeners();
        songIndexAlbum = 0;
        originalAlbum = album;
        currentAlbum = new Album(album.getName(), album.getOwner(), album.getReleaseYear(),
                album.getDescription(), album.getSongs());
        playMode = "album";
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
                    currentSong.decreaseListeners();
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
                        currentSong.decreaseListeners();
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

    private <T extends AudioFilesCollection> int calculateSongIndexCollections(final T audioFile,
                                                                               final T originalFile,
                                                                               final int index,
                                                                           final boolean shuffle) {
        int indexCopy = index;
        boolean shuffleCopy = shuffle;

        while (playTime > audioFile.getSongs().get(indexCopy).getDuration()) {
            // If is the playlist's last song, the player is reset
            if (indexCopy == audioFile.getSongs().size() - 1) {
                originalFile.decreaseListeners();
                originalFile.getSongs().get(indexCopy).decreaseListeners();
                playTime = 0;
                paused = true;
                if (shuffleCopy) {
                    unsuffle();
                }
                playMode = "clear";
                shuffleCopy = false;
                break;
            } else {
                playTime -= audioFile.getSongs().get(indexCopy).getDuration();
                originalFile.getSongs().get(indexCopy).decreaseListeners();
                indexCopy += 1;
                originalFile.getSongs().get(indexCopy).increaseListeners();
            }
        }

        return indexCopy;
    }

    private <T extends AudioFilesCollection> int calculateRepeatAll(final T audioFile,
                                                                    final T originalFile,
                                                                    final int index) {
        int songIndexCopy = index;

        while (playTime > audioFile.getSongs().get(songIndexCopy).getDuration()) {
            playTime -= audioFile.getSongs().get(songIndexCopy).getDuration();
            originalFile.getSongs().get(songIndexCopy).decreaseListeners();
            songIndexCopy = (songIndexCopy + 1) % audioFile.getSongs().size();
            audioFile.getSongs().get(songIndexCopy).increaseListeners();
        }

        return songIndexCopy;
    }

    /**
     * Calculates the player status for a playlist based on the current repeat mode.
     */
    private void calculateStatusPlaylistOrAlbum() {
        switch (repeat) {
            case ("no repeat"):
                if (playMode.equals("album")) {
                    songIndexAlbum = calculateSongIndexCollections(currentAlbum, originalAlbum,
                            songIndexAlbum, shuffleAlbum);
                } else {
                    songIndex = calculateSongIndexCollections(currentPlaylist, originalPlaylist,
                            songIndex, shufflePLaylist);
                }
                break;
            case ("repeat all"):
                // In this case the player does not reset, it always plays the next song
                if (playMode.equals("album")) {
                    songIndexAlbum = calculateRepeatAll(currentAlbum, originalAlbum,
                            songIndexAlbum);
                } else {
                    songIndex = calculateRepeatAll(currentPlaylist, originalPlaylist, songIndex);
                }
                break;
            case ("repeat current song"):
                // In this case the player does not reset, it always plays the same song
                if (playMode.equals("album")) {
                    playTime = playTime
                            - (playTime / currentAlbum.getSongs().get(songIndexAlbum).getDuration())
                            * currentAlbum.getSongs().get(songIndexAlbum).getDuration();
                } else {
                    playTime = playTime
                            - (playTime / currentPlaylist.getSongs().get(songIndex).getDuration())
                            * currentPlaylist.getSongs().get(songIndex).getDuration();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Updates the status of a podcast based on the current repeat mode.
     *
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
                currentPodcast.decreaseListeners();
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
     *
     * @param commandTimestamp The timestamp of the command.
     */
    public void calculateStatus(final Integer commandTimestamp) {
        if (!paused && !playMode.equals("clear") && userStatus.equals("online")) {
            playTime += commandTimestamp - lastCommandTimestamp;
        }

        lastCommandTimestamp = commandTimestamp;

        switch (playMode) {
            case ("clear"):
                break;
            case ("song"):
                calculateStatusSong();
                break;
            case ("album"):
            case ("playlist"):
                calculateStatusPlaylistOrAlbum();
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
     *
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
            case "album":
                newNode.put("name", currentAlbum.getSongs().get(songIndexAlbum).getName());
                newNode.put("remainedTime",
                        currentAlbum.getSongs().get(songIndexAlbum).getDuration() - playTime);
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

        switch (playMode) {
            case ("playlist"):
                newNode.put("shuffle", shufflePLaylist);
                break;
            case "album":
                newNode.put("shuffle", shuffleAlbum);
                break;
            case "song":
            case ("podcast"):
            case ("clear"):
                newNode.put("shuffle", false);
                break;
            default:
                break;
        }

        newNode.put("paused", paused);

        return newNode;
    }

    /**
     * Plays or pauses the player.
     *
     * @param commandTimestamp The timestamp of the command.
     */
    public void playPause(final Integer commandTimestamp) {
        calculateStatus(commandTimestamp);
        paused = !paused;
    }

    /**
     * Changes the repeat mode.
     *
     * @param commandTimestamp The timestamp of the command.
     * @return The new repeat mode.
     */
    public String repeat(final Integer commandTimestamp) {
        calculateStatus(commandTimestamp);

        if (repeat.equals("repeat current song") || repeat.equals("repeat infinite")) {
            repeat = "no repeat";
        } else if ((playMode.equals("playlist") || playMode.equals("album"))
                && repeat.equals("no repeat")) {
            repeat = "repeat all";
        } else if ((playMode.equals("song") || playMode.equals("podcast"))
                && repeat.equals("no repeat")) {
            repeat = "repeat once";
        } else if ((playMode.equals("playlist") || playMode.equals("album"))
                && repeat.equals("repeat all")) {
            repeat = "repeat current song";
        } else if ((playMode.equals("song") || playMode.equals("podcast"))
                && repeat.equals("repeat once")) {
            repeat = "repeat infinite";
        }

        return repeat;
    }

    /**
     * Likes or unlikes a song.
     *
     * @param command The command object.
     * @return True if the song is liked, false otherwise.
     */
    public boolean like(final Command command) {
        calculateStatus(command.getTimestamp());
        lastCommandTimestamp = command.getTimestamp();

        Song song = switch (playMode) {
            case "song" -> currentSong;
            case "playlist" -> currentPlaylist.getSongs().get(songIndex);
            case "album" -> currentAlbum.getSongs().get(songIndexAlbum);
            default -> null;
        };

        if (song == null) {
            return false;
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

    private <T extends AudioFilesCollection> int calculateNextStatus(final T audioFile,
                                                                     final int index,
                                                                     final boolean shuffle,
                                                                     final ObjectNode resultNode) {
        int indexCopy = index;
        boolean shuffleCopy = shuffle;

        if (indexCopy == audioFile.getSongs().size() - 1) {
            playMode = "clear";
            paused = true;
            if (shuffleCopy) {
                unsuffle();
            }
            shuffleCopy = false;
            resultNode.put("message",
                    "Please load a source before skipping to the next track.");
        } else {
            indexCopy++;
            resultNode.put("message",
                    "Skipped to next track successfully. The current track is "
                            + audioFile.getSongs().get(indexCopy).getName() + ".");
            paused = false;
        }
        return indexCopy;
    }

    private void nextPlaylistOrAlbum(final ObjectNode resultNode) {
        switch (repeat) {
            case ("no repeat"):
                if (playMode.equals("album")) {
                    songIndexAlbum = calculateNextStatus(currentAlbum, songIndexAlbum,
                            shuffleAlbum, resultNode);
                } else {
                    songIndex = calculateNextStatus(currentPlaylist, songIndex,
                            shufflePLaylist, resultNode);
                }
                break;
            case ("repeat all"):
                if (playMode.equals("album")) {
                    songIndexAlbum = (songIndexAlbum + 1) % currentAlbum.getSongs().size();
                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentAlbum.getSongs().get(songIndexAlbum).getName() + ".");
                } else {
                    songIndex = (songIndex + 1) % currentPlaylist.getSongs().size();
                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                }
                paused = false;
                break;
            case ("repeat current song"):
                if (playMode.equals("album")) {
                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentAlbum.getSongs().get(songIndexAlbum).getName() + ".");
                } else {
                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                }
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
     *
     * @param command    The given command
     * @param resultNode The JSON object
     */
    public void next(final Command command, final ObjectNode resultNode) {
        calculateStatus(command.getTimestamp());
        switch (playMode) {
            case "song":
                nextSong(resultNode);
                break;
            case "album":
            case "playlist":
                nextPlaylistOrAlbum(resultNode);
                break;
            case "podcast":
                nextPodcast(resultNode);
                break;
            default:
                break;
        }
        playTime = 0;
    }

    /**
     * Skips to the previous track depending on the current player status.
     *
     * @param command    The given command
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
                if (songIndex != 0 && playTime == 0 && !paused && userStatus.equals("online")) {
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
            case "album":
                if (songIndexAlbum != 0 && playTime == 0 && !paused
                        && userStatus.equals("online")) {
                    songIndexAlbum--;
                } else {
                    songIndexAlbum = currentAlbum.getSongs().size() - 1;
                }
                resultNode.put("message",
                        "Returned to previous track successfully. The current track is "
                                + currentAlbum.getSongs().get(songIndexAlbum).getName() + ".");
                break;
            default:
                break;
        }
        playTime = 0;
        paused = false;
    }

    /**
     * Skips forward or backward ninety seconds depending on the current player status.
     *
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
    }

    /**
     * Unsuflles the playlist depending on the current player status.
     */
    private void unsuffle() {
        if (playMode.equals("album")) {
            songIndexAlbum = unsuffledSongsAlbum
                    .indexOf(currentAlbum.getSongs().get(songIndexAlbum));
            currentAlbum.setSongs(unsuffledSongsAlbum);
        } else if (playMode.equals("playlist")) {
            songIndex = unsuffledSongsPlaylist.indexOf(currentPlaylist.getSongs().get(songIndex));
            currentPlaylist.setSongs(unsuffledSongsPlaylist);
        }
    }

    /**
     * Activates or deactivates the shuffle function depending on the current player status.
     *
     * @param command    The given command
     * @param resultNode The JSON object
     */
    public void shuffle(final Command command, final ObjectNode resultNode) {
        calculateStatus(command.getTimestamp());

        if (playMode.equals("clear")) {
            resultNode.put("message",
                    "Please load a source before using the shuffle function.");
            return;
        }

        if (!playMode.equals("playlist") && !playMode.equals("album")) {
            resultNode.put("message",
                    "The loaded source is not a playlist or an album.");
            return;
        }

        if (shuffleAlbum) {
            unsuffle();
            shuffleAlbum = false;

            resultNode.put("message",
                    "Shuffle function deactivated successfully.");
        } else if (shufflePLaylist) {
            unsuffle();
            shufflePLaylist = false;

            resultNode.put("message",
                    "Shuffle function deactivated successfully.");
        } else {
            if (playMode.equals("album")) {
                unsuffledSongsAlbum = new ArrayList<>(currentAlbum.getSongs());
                ArrayList<Song> shuffledSongs = new ArrayList<>(currentAlbum.getSongs());
                Collections.shuffle(shuffledSongs, new Random(command.getSeed()));
                songIndexAlbum = shuffledSongs.indexOf(unsuffledSongsAlbum.get(songIndexAlbum));
                currentAlbum.setSongs(shuffledSongs);
                shuffleAlbum = true;
            } else {
                unsuffledSongsPlaylist = new ArrayList<>(currentPlaylist.getSongs());
                ArrayList<Song> shuffledSongs = new ArrayList<>(currentPlaylist.getSongs());
                Collections.shuffle(shuffledSongs, new Random(command.getSeed()));
                songIndex = shuffledSongs.indexOf(unsuffledSongsPlaylist.get(songIndex));
                currentPlaylist.setSongs(shuffledSongs);
                shufflePLaylist = true;
            }

            resultNode.put("message", "Shuffle function activated successfully.");
        }
    }

    /**
     * Returns the current song.
     *
     * @return The current song.
     */
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Returns the current playlist.
     *
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
    public boolean isShufflePlaylist() {
        return shufflePLaylist;
    }

    /**
     * @return if the player is paused or not.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the player to paused or not.
     *
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
    public ArrayList<Song> getUnsuffledSongsPlaylist() {
        return unsuffledSongsPlaylist;
    }

    /**
     * @return Returns the status of a user
     */
    public void setUserStatus(final String userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * @return Returns the last command timestamp for a user
     */
    public void setLastCommandTimestamp(final Integer lastCommandTimestamp) {
        this.lastCommandTimestamp = lastCommandTimestamp;
    }

    /**
     * @return Returns the current album
     */
    public Album getCurrentAlbum() {
        return currentAlbum;
    }

    /**
     * @return Returns the index of a song from an album.
     */
    public Integer getSongIndexAlbum() {
        return songIndexAlbum;
    }
}
