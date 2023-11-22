package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

import static java.lang.Math.floor;

public class Player {
    private Song currentSong;
    private Playlist currentPlaylist;
    private ArrayList<Song> unsuffledSongs;
    private Podcast currentPodcast;
    private Integer songIndex;
    private Integer lastCommandTimestamp;
    private Integer playTime;
    private String playMode;
    private HashMap<String, PodcastHistory> podcastsHistory;
    private String repeat;
    private boolean shuffle;
    private boolean paused;
    private int pausedTime;
    private ArrayList<Song> likedSongs;

    public Player() {
        podcastsHistory = new HashMap<>();
        likedSongs = new ArrayList<>();
        repeat = "no repeat";
        shuffle = false;
        paused = false;
        playMode = "clear";
    }

    public void resetPlayer(Integer loadTimestamp) {
        CalculateStatus(loadTimestamp);
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

    public void load(Song song, Integer loadTimestamp) {
        CalculateStatus(loadTimestamp);
        currentSong = song;
        lastCommandTimestamp = loadTimestamp;
        playTime = 0;
        playMode = "song";
        paused = false;
        repeat = "no repeat";
        shuffle = false;
    }

    public void load(Playlist playlist, Integer loadTimestamp) {
        if (playlist.getSongs().isEmpty())
            return;
        CalculateStatus(loadTimestamp);
        currentSong = playlist.getSongs().get(0);
        songIndex = 0;
        currentPlaylist = playlist;
        lastCommandTimestamp = loadTimestamp;
        playTime = 0;
        playMode = "playlist";
        paused = false;
        repeat = "no repeat";
        shuffle = false;
    }

    public void load(Podcast podcast, Integer loadTimestamp) {
        CalculateStatus(loadTimestamp);
        playMode = "podcast";
        if (!podcastsHistory.containsKey(podcast.getName())) {
            podcastsHistory.put(podcast.getName(), new PodcastHistory());
        }

        PodcastHistory history = this.podcastsHistory.get(podcast.getName());
        playTime = 0;

        currentPodcast = podcast;
        lastCommandTimestamp = loadTimestamp;
        paused = false;
        repeat = "no repeat";
        shuffle = false;
    }

    private void CalculateStatus(Integer commandTimestamp) {
        if (!paused && !playMode.equals("clear")) {
            playTime += commandTimestamp - lastCommandTimestamp;
        }

        switch (playMode) {
            case ("clear"):
                break;
            case ("song"):
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
                        playTime = playTime - (int) (playTime / currentSong.getDuration()) * currentSong.getDuration();
                        break;
                }
                break;
            case ("playlist"):
                switch (repeat) {
                    case ("no repeat"):
                        while (playTime > currentPlaylist.getSongs().get(songIndex).getDuration()) {
                            if (songIndex == currentPlaylist.getSongs().size() - 1) {
                                playMode = "clear";
                                playTime = 0;
                                paused = true;
                                shuffle = false;
                                break;
                            } else {
                                playTime = playTime - currentPlaylist.getSongs().get(songIndex).getDuration();
                                songIndex += 1;
                            }
                        }
                        break;
                    case ("repeat all"):
                        while (playTime > currentPlaylist.getSongs().get(songIndex).getDuration()) {
                            playTime = playTime - currentPlaylist.getSongs().get(songIndex).getDuration();
                            songIndex = (songIndex + 1) % currentPlaylist.getSongs().size();
                        }
                        break;
                    case ("repeat current song"):
                        playTime = playTime - (int) (playTime / currentPlaylist.getSongs().get(songIndex).getDuration()) * currentPlaylist.getSongs().get(songIndex).getDuration();
                        break;
                }
                break;
            case ("podcast"):
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                switch (repeat) {
                    case ("no repeat"):
                        while (playTime > currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond()) {
                            if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                                history.setLastEpisode(0);
                                history.setSecond(0);
                                playMode = "clear";
                                playTime = 0;
                                paused = true;
                                break;
                            } else {
                                playTime = playTime - (currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond());
                                history.setLastEpisode(history.getLastEpisode() + 1);
                                history.setSecond(0);
                            }
                        }
                        break;
                    case ("repeat once"):
                        while (playTime > currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond()) {
                            if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                                history.setLastEpisode(0);
                                history.setSecond(0);
                                repeat = "no repeat";
                                playTime = playTime - currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond();

                                while (playTime > currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond()) {
                                    if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                                        history.setLastEpisode(0);
                                        history.setSecond(0);
                                        playMode = "clear";
                                        playTime = 0;
                                        paused = true;
                                        break;
                                    } else {
                                        playTime = playTime - (currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond());
                                        history.setLastEpisode(history.getLastEpisode() + 1);
                                        history.setSecond(0);
                                    }
                                }
                                break;
                            } else {
                                playTime = playTime - (currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond());
                                history.setLastEpisode(history.getLastEpisode() + 1);
                                history.setSecond(0);
                            }
                        }
                        break;
                    case ("repeat infinite"):
                        while (playTime > currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond()) {
                            playTime = playTime - (currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - history.getSecond());
                            history.setLastEpisode((history.getLastEpisode() + 1) % currentPodcast.getEpisodes().size());
                            history.setSecond(0);
                        }
                        break;
                }
                break;
        }
    }

    public ObjectNode status(Integer commandTimestamp) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode newNode = objectMapper.createObjectNode();
        CalculateStatus(commandTimestamp);

        switch (playMode) {
            case ("song"):
                newNode.put("name", currentSong.getName());
                newNode.put("remainedTime", currentSong.getDuration() - playTime);
                break;
            case ("playlist"):
                newNode.put("name", currentPlaylist.getSongs().get(songIndex).getName());
                newNode.put("remainedTime", currentPlaylist.getSongs().get(songIndex).getDuration() - playTime);
                break;
            case ("podcast"):
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                newNode.put("name", currentPodcast.getEpisodes().get(history.getLastEpisode()).getName());
                newNode.put("remainedTime", currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - playTime - history.getSecond());
                break;
            case ("clear"):
                newNode.put("name", "");
                newNode.put("remainedTime", playTime);
                break;
        }
        if (repeat.equals("no repeat")) {
            newNode.put("repeat", "No Repeat");
        } else if (repeat.equals("repeat all")) {
            newNode.put("repeat", "Repeat All");
        } else if (repeat.equals("repeat current song")) {
            newNode.put("repeat", "Repeat Current Song");
        } else if (repeat.equals("repeat once")) {
            newNode.put("repeat", "Repeat Once");
        } else if (repeat.equals("repeat infinite")) {
            newNode.put("repeat", "Repeat Infinite");
        }

        newNode.put("shuffle", shuffle);
        newNode.put("paused", paused);
        lastCommandTimestamp = commandTimestamp;

        return newNode;
    }

    public void playPause(Integer commandTimestamp) {
        CalculateStatus(commandTimestamp);
        paused = !paused;
        lastCommandTimestamp = commandTimestamp;
    }

    public String repeat(Integer commandTimestamp) {
        CalculateStatus(commandTimestamp);

        if (repeat.equals("repeat current song") || repeat.equals("repeat infinite")) {
            repeat = "no repeat";
        } else if (playMode.equals("playlist") && repeat.equals("no repeat")) {
            repeat = "repeat all";
        } else if ((playMode.equals("song") || playMode.equals("podcast")) && repeat.equals("no repeat")) {
            repeat = "repeat once";
        } else if (playMode.equals("playlist") && repeat.equals("repeat all")) {
            repeat = "repeat current song";
        } else if ((playMode.equals("song") || playMode.equals("podcast")) && repeat.equals("repeat once")) {
            repeat = "repeat infinite";
        }

        lastCommandTimestamp = commandTimestamp;
        return repeat;
    }

    public boolean like(Command command) {
        CalculateStatus(command.getTimestamp());
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
        currentSong.likeSong();
        return true;
    }

    public void next(Command command, ObjectNode resultNode) {
        CalculateStatus(command.getTimestamp());
        switch (playMode) {
            case "song":
                switch (repeat) {
                    case ("no repeat"):
                        playMode = "clear";
                        paused = true;
                        resultNode.put("message", "Please load a source before skipping to the next track.");
                        break;
                    case ("repeat once"):
                        repeat = "no repeat";
                        resultNode.put("message", "Skipped to next track successfully. The current track is " + currentSong.getName() + ".");
                        paused = false;
                        break;
                    case ("repeat infinite"):
                        resultNode.put("message", "Skipped to next track successfully. The current track is " + currentSong.getName() + ".");
                        paused = false;
                        break;
                }
                playTime = 0;
                break;
            case "playlist":
                switch (repeat) {
                    case ("no repeat"):
                        if (songIndex == currentPlaylist.getSongs().size() - 1) {
                            playMode = "clear";
                            paused = true;
                            shuffle = false;
                            resultNode.put("message", "Please load a source before skipping to the next track.");
                        } else {
                            songIndex++;
                            resultNode.put("message", "Skipped to next track successfully. The current track is " + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                            paused = false;
                        }
                        break;
                    case ("repeat all"):
                        songIndex = (songIndex + 1) % currentPlaylist.getSongs().size();
                        resultNode.put("message", "Skipped to next track successfully. The current track is " + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                        paused = false;
                        break;
                    case ("repeat current song"):
                        resultNode.put("message", "Skipped to next track successfully. The current track is " + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                        paused = false;
                        break;
                }
                playTime = 0;
                break;
            case "podcast":
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                switch (repeat) {
                    case ("no repeat"):
                        if (history.getLastEpisode() == currentPodcast.getEpisodes().size() - 1) {
                            playMode = "clear";
                            paused = true;
                            history.setSecond(0);
                            history.setLastEpisode(0);
                            resultNode.put("message", "Please load a source before skipping to the next track.");
                        } else {
                            history.setSecond(0);
                            history.setLastEpisode(history.getLastEpisode() + 1);
                            resultNode.put("message", "Skipped to next track successfully. The current track is " +
                                    currentPodcast.getEpisodes().get(history.getLastEpisode()).getName() + ".");
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
                        resultNode.put("message", "Skipped to next track successfully. The current track is " +
                                currentPodcast.getEpisodes().get(history.getLastEpisode()).getName() + ".");
                        paused = false;
                        break;
                    case ("repeat infinite"):
                        history.setLastEpisode((history.getLastEpisode() + 1) % currentPodcast.getEpisodes().size());
                        history.setSecond(0);
                        resultNode.put("message", "Skipped to next track successfully. The current track is " +
                                currentPodcast.getEpisodes().get(history.getLastEpisode()).getName() + ".");
                        paused = false;
                        break;
                }
                playTime = 0;
                break;
        }
        lastCommandTimestamp = command.getTimestamp();
    }

    public void prev(Command command, ObjectNode resultNode) {
        CalculateStatus(command.getTimestamp());
        switch (playMode) {
            case "song":
                resultNode.put("message", "Returned to previous track successfully. The current track is " + currentSong.getName() + ".");
                break;
            case "playlist":
                System.out.println("----------------------------------------------");
                System.out.println("timestamp = " + command.getTimestamp() + "user = " + command.getUsername());
                System.out.println("songIndex = " + songIndex + " playTime = " + playTime + " paused = " + paused);


                if (songIndex != 0 && playTime == 0 && !paused) {
                    songIndex--;
                }
                resultNode.put("message", "Returned to previous track successfully. The current track is " + currentPlaylist.getSongs().get(songIndex).getName() + ".");
                break;
            case "podcast":
                PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
                if (history.getLastEpisode() != 0 && history.getSecond() + playTime == 0) {
                    history.setLastEpisode(history.getLastEpisode() - 1);
                }
                history.setSecond(0);
                resultNode.put("message", "Returned to previous track successfully. The current track is " +
                        currentPodcast.getEpisodes().get(history.getLastEpisode()).getName() + ".");
                break;
        }
        playTime = 0;
        paused = false;
        lastCommandTimestamp = command.getTimestamp();
        System.out.println("songIndex = " + songIndex + " playTime = " + playTime + " paused = " + paused);
        System.out.println("----------------------------------------------");
    }

    public void forwardBackward(Command command) {
        CalculateStatus(command.getTimestamp());
        PodcastHistory history = podcastsHistory.get(currentPodcast.getName());
        if (command.getCommand().equals("forward")) {
            int remainedTime = currentPodcast.getEpisodes().get(history.getLastEpisode()).getDuration() - playTime - history.getSecond();
            if (remainedTime <= 90 && history.getLastEpisode() != currentPodcast.getEpisodes().size() - 1) {
                history.setLastEpisode(history.getLastEpisode() + 1);
                history.setSecond(0);
                playTime = 0;
            } else if (remainedTime > 90 && history.getLastEpisode() != currentPodcast.getEpisodes().size() - 1) {
                playTime += 90;
            } else {
                history.setLastEpisode(0);
                history.setSecond(0);
                playTime = 0;
            }
        } else {
            int currentTime = playTime + history.getSecond();
            if (currentTime <= 90) {
                history.setSecond(0);
                playTime = 0;
            } else {
                playTime -= 90;
            }
        }

        lastCommandTimestamp = command.getTimestamp();
    }

    public void shuffle(Command command, ObjectNode resultNode) {
        CalculateStatus(command.getTimestamp());

        if (playMode.equals("clear")) {
            resultNode.put("message", "Please load a source before using the shuffle function.");
            return;
        }

        if (!playMode.equals("playlist")) {
            resultNode.put("message", "The loaded source is not a playlist.");
            return;
        }

        if (shuffle) {
            songIndex = unsuffledSongs.indexOf(currentPlaylist.getSongs().get(songIndex));
            currentPlaylist.setSongs(unsuffledSongs);
            shuffle = false;
            resultNode.put("message", "Shuffle function deactivated successfully.");
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

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public Podcast getCurrentPodcast() {
        return currentPodcast;
    }

    public void setCurrentPodcast(Podcast currentPodcast) {
        this.currentPodcast = currentPodcast;
    }

    public Integer getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(Integer songIndex) {
        this.songIndex = songIndex;
    }

    public Integer getLastCommandTimestamp() {
        return lastCommandTimestamp;
    }

    public void setLastCommandTimestamp(Integer lastCommandTimestamp) {
        this.lastCommandTimestamp = lastCommandTimestamp;
    }

    public Integer getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Integer playTime) {
        this.playTime = playTime;
    }

    public String getPlayMode() {
        return playMode;
    }

    public void setPlayMode(String playMode) {
        this.playMode = playMode;
    }

    public HashMap<String, PodcastHistory> getPodcastsHistory() {
        return podcastsHistory;
    }

    public void setPodcastsHistory(HashMap<String, PodcastHistory> podcastsHistory) {
        this.podcastsHistory = podcastsHistory;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getPausedTime() {
        return pausedTime;
    }

    public void setPausedTime(int pausedTime) {
        this.pausedTime = pausedTime;
    }

    public ArrayList<Song> getLikedSongs() {
        return likedSongs;
    }

    public void setLikedSongs(ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }
}
