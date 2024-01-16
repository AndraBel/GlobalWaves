package app.users.userComponents;

import app.users.user.UsersHistory;
import app.admin.Library;
import app.users.artist.Artist;
import app.users.Host;
import app.users.user.User;
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
import java.util.LinkedHashMap;
import java.util.Map;
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
    private UsersHistory usersHistory;
    private boolean isPremium;
    private LinkedHashMap<Song, Integer> songsAdBreak;
    private boolean nextAdBreak;
    private Library library;
    private User user;
    private Integer adPrice;
    private Song adSong;
    private static final int RANDOMTIME = 30;

    private String previousMode;

    public Player(final UsersHistory usersHistory,
                  final boolean isPremium,
                  final Library library,
                  final User user) {
        podcastsHistory = new HashMap<>();
        likedSongs = new ArrayList<>();
        repeat = "no repeat";
        shufflePLaylist = false;
        shuffleAlbum = false;
        paused = false;
        playMode = "clear";
        userStatus = "online";
        lastCommandTimestamp = 0;
        this.usersHistory = usersHistory;
        this.isPremium = isPremium;
        songsAdBreak = new LinkedHashMap<>();
        nextAdBreak = false;
        this.library = library;
        this.user = user;
        adPrice = 0;
        adSong = null;
        previousMode = "none";

        for (Song song : library.getSongs()) {
            if (song.getGenre().equals("advertisement")) {
                adSong = song;
            }
        }
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

        if (previousMode.equals("none")) {
            playTime = 0;
        }
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

        if (song != adSong) {
            addSongAdBreak(song);
        }
        nextAdBreak = false;

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
        currentSong = playlist.getSongs().getFirst();
        usersHistory.addSong(currentSong);

        addSongAdBreak(currentSong);

        if (isPremium) {
            usersHistory.addSongPremium(currentSong);
        }

        calculateStatus(loadTimestamp);

        nextAdBreak = false;

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
        usersHistory.addEpisode(podcast.getEpisodes().getFirst());

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
        currentSong = album.getSongs().getFirst();
        usersHistory.addSong(currentSong);

        if (isPremium) {
            usersHistory.addSongPremium(currentSong);
        }
        addSongAdBreak(currentSong);

        calculateStatus(loadTimestamp);
        nextAdBreak = false;

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
    private void calculateStatusSong(final Integer commandTimestamp) {
        switch (repeat) {
            case ("no repeat"):
                if (playTime >= currentSong.getDuration()) {
                    if (currentSong == adSong) {
                        if (previousMode.equals("album")) {
                            playMode = "album";
                            playTime = playTime - currentSong.getDuration();
                            return;
                        } else if (previousMode.equals("playlist")) {
                            playMode = "playlist";
                            playTime = playTime - currentSong.getDuration();
                            return;
                        }
                    }

                    playMode = "clear";
                    playTime = 0;
                    paused = true;
                    currentSong.decreaseListeners();

                    if (nextAdBreak) {
                        this.previousMode = "none";
                        nextAdBreak = false;
                        library.calculateAdBreak(user, adPrice);

                        load(adSong, commandTimestamp);
                        return;
                    }
                }
                break;
            case ("repeat once"):
                if (playTime >= currentSong.getDuration()) {
                    playTime -= currentSong.getDuration();
                    repeat = "no repeat";

                    usersHistory.addSong(currentSong);
                    addSongAdBreak(currentSong);

                    if (isPremium) {
                        usersHistory.addSongPremium(currentSong);
                    }

                    if (playTime >= currentSong.getDuration()) {
                        playMode = "clear";
                        playTime = 0;
                        paused = true;
                        currentSong.decreaseListeners();
                    }
                }
                break;
            case ("repeat infinite"):
                int count = (int) Math.ceil((double) playTime / currentSong.getDuration());

                for (int i = 0; i < count; i++) {
                    usersHistory.addSong(currentSong);
                }

                if (isPremium) {
                    for (int i = 0; i < count; i++) {
                        usersHistory.addSongPremium(currentSong);
                    }
                }

                playTime = playTime - (playTime / currentSong.getDuration())
                        * currentSong.getDuration();
                break;
            default:
                break;
        }
    }

    private <T extends AudioFilesCollection> int
    calculateSongIndexCollections(final T audioFile,
                                  final T originalFile,
                                  final int index,
                                  final boolean shuffle,
                                  final Integer commandTimestamp,
                                  final String newPreviousMode) {
        int indexCopy = index;
        boolean shuffleCopy = shuffle;

        while (playTime >= audioFile.getSongs().get(indexCopy).getDuration()) {
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

                if (nextAdBreak) {
                    this.previousMode = "none";
                    nextAdBreak = false;
                    library.calculateAdBreak(user, adPrice);

                    load(adSong, commandTimestamp);
                }

                break;
            } else {
                playTime -= audioFile.getSongs().get(indexCopy).getDuration();
                originalFile.getSongs().get(indexCopy).decreaseListeners();
                indexCopy += 1;
                originalFile.getSongs().get(indexCopy).increaseListeners();

                usersHistory.addSong(originalFile.getSongs().get(indexCopy));
                if (isPremium) {
                    usersHistory.addSongPremium(originalFile.getSongs().get(indexCopy));
                }

                if (nextAdBreak) {
                    nextAdBreak = false;
                    library.calculateAdBreak(user, adPrice);
                    this.previousMode = newPreviousMode;
                    load(adSong, commandTimestamp);
                } else {
                    addSongAdBreak(originalFile.getSongs().get(indexCopy));
                }
            }
        }

        return indexCopy;
    }

    private <T extends AudioFilesCollection> int calculateRepeatAll(final T audioFile,
                                                                    final T originalFile,
                                                                    final int index) {
        int songIndexCopy = index;

        while (playTime >= audioFile.getSongs().get(songIndexCopy).getDuration()) {
            playTime -= audioFile.getSongs().get(songIndexCopy).getDuration();
            originalFile.getSongs().get(songIndexCopy).decreaseListeners();
            songIndexCopy = (songIndexCopy + 1) % audioFile.getSongs().size();
            audioFile.getSongs().get(songIndexCopy).increaseListeners();

            usersHistory.addSong(originalFile.getSongs().get(songIndexCopy));
            if (isPremium) {
                usersHistory.addSongPremium(originalFile.getSongs().get(songIndexCopy));
            }
            addSongAdBreak(originalFile.getSongs().get(songIndexCopy));
        }

        return songIndexCopy;
    }

    /**
     * Calculates the player status for a playlist based on the current repeat mode.
     */
    private void calculateStatusPlaylistOrAlbum(final Integer commandTimestamp) {
        switch (repeat) {
            case ("no repeat"):
                if (playMode.equals("album")) {
                    songIndexAlbum = calculateSongIndexCollections(currentAlbum, originalAlbum,
                            songIndexAlbum, shuffleAlbum, commandTimestamp, "album");
                } else {
                    songIndex = calculateSongIndexCollections(currentPlaylist, originalPlaylist,
                            songIndex, shufflePLaylist, commandTimestamp, "playlist");
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
                    calculateRepeatCurr(currentAlbum.getSongs(), songIndexAlbum);
                } else {
                    calculateRepeatCurr(currentPlaylist.getSongs(), songIndex);
                }
                break;
            default:
                break;
        }
    }

    private void calculateRepeatCurr(final ArrayList<Song> songs, final Integer currSongIndex) {
        int count = (int) Math.ceil((double) playTime
                / songs.get(currSongIndex).getDuration());

        for (int i = 0; i < count; i++) {
            usersHistory.addSong(songs.get(currSongIndex));
        }

        if (isPremium) {
            for (int i = 0; i < count; i++) {
                usersHistory.addSongPremium(songs.get(currSongIndex));
            }
        }

        playTime = playTime
                - (playTime / songs.get(currSongIndex).getDuration())
                * songs.get(currSongIndex).getDuration();
    }

    /**
     * Updates the status of a podcast based on the current repeat mode.
     *
     * @param history The PodcastHistory object for the current podcast.
     */
    private void updateStatusPodcast(final PodcastHistory history) {
        while (playTime >= currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration()
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

                usersHistory.addEpisode(currentPodcast.getEpisodes().get(history.getLastEpisode()));

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
                while (playTime >= duration - history.getSecond()) {
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

                        usersHistory.addPodcast(currentPodcast);

                        break;
                    } else {
                        playTime = playTime - (duration - history.getSecond());
                        history.setLastEpisode(history.getLastEpisode() + 1);

                        usersHistory.addEpisode(currentPodcast.getEpisodes()
                                .get(history.getLastEpisode()));

                        history.setSecond(0);
                    }
                    duration = currentPodcast.getEpisodes().get(history.getLastEpisode())
                            .getDuration();
                }
                break;
            case ("repeat infinite"):
                // Repeats the podcast infinitely and calculates the play time
                duration = currentPodcast.getEpisodes()
                        .get(history.getLastEpisode()).getDuration();
                while (playTime >= duration - history.getSecond()) {
                    playTime = playTime - (duration - history.getSecond());
                    history.setLastEpisode((history.getLastEpisode() + 1)
                            % currentPodcast.getEpisodes().size());

                    usersHistory.addEpisode(currentPodcast.getEpisodes()
                            .get(history.getLastEpisode()));

                    if (history.getLastEpisode() == 0) {
                        usersHistory.addPodcast(currentPodcast);
                    }

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
                calculateStatusSong(commandTimestamp);
                break;
            case ("album"):
            case ("playlist"):
                calculateStatusPlaylistOrAlbum(commandTimestamp);
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
                newNode.put("name", currentAlbum.getSongs()
                        .get(songIndexAlbum).getName());
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
            case "repeat current song" -> newNode.put("repeat",
                    "Repeat Current Song");
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

                usersHistory.addSong(currentSong);

                break;
            case ("repeat infinite"):
                resultNode.put("message",
                        "Skipped to next track successfully. The current track is "
                                + currentSong.getName() + ".");
                usersHistory.addSong(currentSong);

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
            usersHistory.addSong(audioFile.getSongs().get(indexCopy));
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

                    usersHistory.addSong(currentAlbum.getSongs().get(songIndexAlbum));

                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentAlbum.getSongs().get(songIndexAlbum).getName() + ".");
                } else {
                    songIndex = (songIndex + 1) % currentPlaylist.getSongs().size();

                    usersHistory.addSong(currentPlaylist.getSongs().get(songIndex));

                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                }
                paused = false;
                break;
            case ("repeat current song"):
                if (playMode.equals("album")) {
                    usersHistory.addSong(currentAlbum.getSongs().get(songIndexAlbum));

                    resultNode.put("message",
                            "Skipped to next track successfully. The current track is "
                                    + currentAlbum.getSongs().get(songIndexAlbum).getName() + ".");
                } else {
                    usersHistory.addSong(currentPlaylist.getSongs().get(songIndex));

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

                    usersHistory.addEpisode(currentPodcast.getEpisodes()
                            .get(history.getLastEpisode()));

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
                    usersHistory.addEpisode(currentPodcast.getEpisodes()
                            .get(history.getLastEpisode()));
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

                usersHistory.addEpisode(currentPodcast.getEpisodes()
                        .get(history.getLastEpisode()));

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
                usersHistory.addSong(currentSong);

                resultNode.put("message",
                        "Returned to previous track successfully. The current track is "
                                + currentSong.getName() + ".");
                break;
            case "playlist":
                if (songIndex != 0 && playTime == 0 && !paused && userStatus.equals("online")) {
                    songIndex--;
                    usersHistory.addSong(currentPlaylist.getSongs().get(songIndex));
                }
                resultNode.put("message",
                        "Returned to previous track successfully. The current track is "
                                + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                break;
            case "podcast":
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                if (history.getLastEpisode() != 0 && history.getSecond() + playTime == 0) {
                    history.setLastEpisode(history.getLastEpisode() - 1);

                    usersHistory.addEpisode(currentPodcast.getEpisodes()
                            .get(history.getLastEpisode()));
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

                usersHistory.addSong(currentAlbum.getSongs().get(songIndexAlbum));

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

                usersHistory.addEpisode(currentPodcast.getEpisodes()
                        .get(history.getLastEpisode()));

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
     * Selects a random song from a list of songs based on the current play mode and time.
     *
     * @param allSongs         List of all available songs.
     * @param commandTimestamp Timestamp of the current command, used for randomization.
     * @return Song A randomly selected song or null if conditions are not met.
     */
    public Song randomSong(final ArrayList<Song> allSongs, final Integer commandTimestamp) {
        calculateStatus(commandTimestamp);

        String genre = null;
        Song randomSong = null;

        if (playMode.equals("song")) {
            if (playTime >= RANDOMTIME) {
                genre = currentSong.getGenre();
                ArrayList<Song> sameGenreSongs = new ArrayList<>();

                for (Song song : allSongs) {
                    if (song.getGenre().equals(genre)) {
                        sameGenreSongs.add(song);
                    }
                }

                if (!sameGenreSongs.isEmpty()) {
                    Random random = new Random(playTime);
                    int randomIndex = random.nextInt(sameGenreSongs.size());
                    randomSong = sameGenreSongs.get(randomIndex);
                }
            }
        }
        return randomSong;
    }

    /**
     * Retrieves the current artist based on the play mode.
     *
     * @return Artist The current artist or null if not applicable.
     */
    public Artist getCurrentArtist() {
        switch (playMode) {
            case "song" -> {
                String artistName = currentSong.getArtist();
                return library.findArtist(artistName);
            }
            case "album" -> {
                String artistName = currentAlbum.getArtist();
                return library.findArtist(artistName);
            }
            case "playlist" -> {
                String artistName = currentPlaylist.getSongs().get(songIndex).getArtist();
                return library.findArtist(artistName);
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Retrieves the current host if the play mode is set to 'podcast'.
     *
     * @return Host The host of the current podcast or null if play mode is not 'podcast'.
     */
    public Host getCurrentHost() {
        if (playMode.equals("podcast")) {
            String hostName = currentPodcast.getOwner();
            return library.findHost(hostName);
        }
        return null;
    }

    /**
     * Adds a song to the ad break count map, incrementing the count if the song already exists.
     * This method is used to keep track of how many times a song has been played during ad breaks.
     *
     * @param song The song to add or increment in the ad break map.
     */
    private void addSongAdBreak(final Song song) {
        if (songsAdBreak.containsKey(song)) {
            // If the song is already in the map, increment the count
            int count = songsAdBreak.get(song);
            songsAdBreak.replace(song, count + 1);
        } else {
            songsAdBreak.put(song, 1);
        }
    }


    /**
     * Sets the flag for an upcoming ad break.
     * This method indicates that an ad break is to be expected next in the playback.
     */
    public void setAdBreak() {
        nextAdBreak = true;
    }

    /**
     * Clears the list of songs played during ad breaks.
     * This method is used to reset the count of songs played during ad breaks.
     */
    public void clearSongsAdBreak() {
        songsAdBreak.clear();
    }

    /**
     * Calculates the total number of songs played during ad breaks.
     * This method sums up all the individual song counts in the ad break song map.
     *
     * @return Integer Total number of songs played during ad breaks.
     */
    public Integer totalAdBreakSongs() {
        Integer total = 0;
        for (Map.Entry<Song, Integer> entry : songsAdBreak.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    /**
     * Retrieves the map of songs played during ad breaks along with their counts.
     *
     * @return LinkedHashMap<Song, Integer> Map of songs and their play counts during ad breaks.
     */
    public LinkedHashMap<Song, Integer> getSongsAdBreak() {
        return songsAdBreak;
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

    /**
     * Sets the premium status of a user or entity.
     *
     * @param premium A boolean value representing the premium status to be set.
     */
    public void setPremium(final boolean premium) {
        isPremium = premium;
    }

    /**
     * Sets the price for an advertisement.
     *
     * @param adPrice The price of the advertisement to be set as an Integer.
     */
    public void setAdPrice(final Integer adPrice) {
        this.adPrice = adPrice;
    }

}
