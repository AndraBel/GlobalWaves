package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes;

    public Podcast(String name, String owner, ArrayList<Episode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
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

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }
}
