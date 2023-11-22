package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.*;

import java.util.*;

public final class Library {
    private ArrayList<Song> songs;
    private ArrayList<Podcast> podcasts;
    private LinkedHashMap<String, User> users;

    private ArrayList<Playlist> publicPlaylists;

    public Library(LibraryInput library) {
        publicPlaylists = new ArrayList<>();

        songs = new ArrayList<>();
        for (SongInput song : library.getSongs()) {
            Song newSong = new Song(song.getName(), song.getDuration(), song.getAlbum(), song.getTags(),
                    song.getLyrics(), song.getGenre(), song.getReleaseYear(), song.getArtist());
            songs.add(newSong);
        }

        podcasts = new ArrayList<>();
        for (PodcastInput podcast : library.getPodcasts()) {
            ArrayList<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episode : podcast.getEpisodes()) {
                episodes.add(new Episode(episode.getName(), episode.getDuration(), episode.getDescription()));
            }
            Podcast newPodcast = new Podcast(podcast.getName(), podcast.getOwner(), episodes);
            podcasts.add(newPodcast);
        }

        users = new LinkedHashMap<>();
        for (UserInput user : library.getUsers()) {
            User newUser = new User(user.getUsername(), user.getAge(), user.getCity(), songs, podcasts, publicPlaylists);
            users.put(newUser.getUsername(), newUser);
        }
    }

    public ObjectNode getTop5Songs(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("timestamp", command.getTimestamp());

        ArrayList<Song> sortedSongs = new ArrayList<>(songs);

        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (int i = 0; i < Math.min(5, sortedSongs.size()); i++) {
            resultsArray.add(sortedSongs.get(i).getName());
        }
        resultNode.put("result", resultsArray);
        return resultNode;
    }

    public ObjectNode getTop5Playlists(Command command) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resultNode = objectMapper.createObjectNode();

        resultNode.put("command", command.getCommand());
        resultNode.put("timestamp", command.getTimestamp());

        ArrayList<Playlist> sortedPLaylist = new ArrayList<>(publicPlaylists);

        sortedPLaylist.sort(Comparator.comparingInt(Playlist::getFollowers).reversed());

        ArrayNode resultsArray = objectMapper.createArrayNode();
        for (int i = 0; i < Math.min(5, sortedPLaylist.size()); i++) {
            resultsArray.add(sortedPLaylist.get(i).getName());
        }
        resultNode.put("result", resultsArray);
        return resultNode;
    }
    public ArrayList<Song> getSongs() {
        return songs;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public LinkedHashMap<String, User> getUsers() {
        return users;
    }

    public ArrayList<Playlist> getPublicPlaylists() {
        return publicPlaylists;
    }
}
