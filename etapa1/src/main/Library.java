package main;

import fileio.input.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
