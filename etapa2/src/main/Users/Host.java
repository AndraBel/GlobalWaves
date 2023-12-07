package main.Users;

import main.Announcement;
import main.Episode;
import main.Library;
import main.Podcast;
import main.UserPages.HostPage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Host {
    private ArrayList<Podcast> allPodcasts;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> allAnnouncements;
    private ArrayList<Announcement> announcements;
    private HostPage hostPage;

    public Host(ArrayList<Podcast> podcasts, ArrayList<Announcement> allAnouncements) {
        allPodcasts = podcasts;
        this.podcasts = new ArrayList<>();
        this.allAnnouncements = allAnouncements;
        announcements = new ArrayList<>();
        hostPage = new HostPage(this.podcasts, this.announcements);
    }

    private boolean hasUniqueEpisodeName(ArrayList<Episode> episodes) {
        Set<String> episodeSet = new HashSet<>();

        for (Episode episode : episodes) {
            if (!episodeSet.add(episode.getName())) {
                return false; // Duplicate episode name found
            }
        }
//        for (Podcast podcast : podcasts) {
//            for (Episode episode : podcast.getEpisodes()) {
//                if (episode.getName().equals(episodeName)) {
//                    return false; // Duplicate episode name found
//                }
//            }
//        }

        return true; // Episode name is unique
    }

    public Integer addPodcast(Podcast podcast) {
        for (Podcast p : podcasts) {
            if (p.getName().equals(podcast.getName())) {
                return 0; // Duplicate podcast found
            }
        }
        if (!hasUniqueEpisodeName(podcast.getEpisodes())) {
            return 1; // Duplicate episode found
        }

        podcasts.add(podcast);
        allPodcasts.add(podcast);
        return 2;
    }

    public Integer addAnnouncement(Announcement announcement) {
        for (Announcement announce : allAnnouncements) {
            if (announce.getName().equals(announcement.getName())
                && announce.getName().equals(announcement.getName())) {
                return 0; // Duplicate announcement found
            }
        }

        announcements.add(announcement);
        allAnnouncements.add(announcement);
        return 1;
    }

    public Integer removeAnnouncement(String announcementName) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(announcementName)) {
                announcements.remove(announcement);
                allAnnouncements.remove(announcement);
                return 0;
            }
        }
        return 1;
    }

    public Integer removePodcast(Podcast podcast) {
        if (!podcasts.contains(podcast)) {
            return 0;
        }
        if (podcast.getListeners() > 0) {
            return 1;
        }
        podcasts.remove(podcast);
        allPodcasts.remove(podcast);
        return 2;
    }

    public HostPage getHostPage() {
        return hostPage;
    }

    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }
}
