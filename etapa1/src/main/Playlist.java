package main;

import fileio.input.EpisodeInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Playlist {
    private String name;
    private String owner;
    private ArrayList<Song> songs;
    private ArrayList<Episode> episodes;
    private boolean visibility;
    private int followers;

    public Playlist(String name, String owner) {
        this.name = name;
        this.owner = owner;
        this.songs = new ArrayList<>();
        this.episodes = new ArrayList<>();
        this.visibility = true;
        followers = 0;
    }

    public boolean matchFilters (HashMap<String, Object> filters) {
        for (Map.Entry<String, Object> filter: filters.entrySet()) {
            switch (filter.getKey()) {
                case "name":
                    if (!this.name.startsWith((String) filter.getValue()))
                        return false;
                    break;
                case "owner":
                    if (!this.owner.equals((String) filter.getValue()))
                        return false;
                    break;
            }
        }
        return true;
    }

    public void follow () {
        followers++;
    }

    public void unfollow () {
        followers--;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
