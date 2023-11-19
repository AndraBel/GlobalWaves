package main;

public class PodcastHistory {
    private Integer lastEpisode;
    private Integer second;

    public PodcastHistory() {
        lastEpisode = 0;
        second = 0;
    }

    public Integer getLastEpisode() {
        return lastEpisode;
    }

    public void setLastEpisode(Integer lastEpisode) {
        this.lastEpisode = lastEpisode;
    }

    public Integer getSecond() {
        return second;
    }

    public void setSecond(Integer second) {
        this.second = second;
    }
}
