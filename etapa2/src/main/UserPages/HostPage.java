package main.UserPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.*;

import java.util.ArrayList;
import java.util.List;

public class HostPage extends Page {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
    private Integer listeners;

    public HostPage(ArrayList<Podcast> podcasts, ArrayList<Announcement> announcements) {
        super(null, null);
        this.podcasts = podcasts;
        this.announcements = announcements;
        listeners = 0;
    }

    @Override
    public void getContent(ObjectNode resultNode) {
        List<String> podcastDetails = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            List<String> episodeDetails = new ArrayList<>();
            for (Episode episode : podcast.getEpisodes()) {
                String episodeInfo = String.format("%s - %s", episode.getName(), episode.getDescription());
                episodeDetails.add(episodeInfo);
            }

            String podcastInfo = String.format("%s:\n\t[%s]\n", podcast.getName(), String.join(", ", episodeDetails));
            podcastDetails.add(podcastInfo);
        }

        List<String> announcementDetails = new ArrayList<>();
        if (announcements == null) {
            announcementDetails.add("[]");
        } else {
            for (Announcement announcement : announcements) {
                String announcementInfo = String.format("%s:\n\t%s\n", announcement.getName(), announcement.getDescription());
                announcementDetails.add(announcementInfo);
            }
        }

        resultNode.put("message", String.format("Podcasts:\n\t[%s]\n\nAnnouncements:\n\t[%s]",
                String.join(", ", podcastDetails), String.join(", ", announcementDetails)));

    }
        public void increaseListeners() {
        listeners++;
    }
    public void decreaseListeners() {
        listeners--;
    }
    public Integer getListeners() {
        return listeners;
    }
    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }
}
