package app.users;

import app.users.user.Observer;
import app.users.user.User;
import app.users.userComponents.publicity.Announcement;
import app.audioFiles.podcasts.Episode;
import app.audioFiles.podcasts.Podcast;
import app.userPages.HostPage;

import java.util.*;

public class Host implements Subject {
    private String name;
    private ArrayList<Podcast> allPodcasts;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> allAnnouncements;
    private ArrayList<Announcement> announcements;
    private HostPage hostPage;
    private Integer listeners;
    private ArrayList<User> listenersList;
    private List<app.users.user.Observer> observers;

    public Host(final ArrayList<Podcast> podcasts, final ArrayList<Announcement> allAnouncements,
                final String username) {
        allPodcasts = podcasts;
        this.podcasts = new ArrayList<>();
        this.allAnnouncements = allAnouncements;
        announcements = new ArrayList<>();
        hostPage = new HostPage(this.podcasts, this.announcements, username);
        name = username;
        listeners = 0;
        listenersList = new ArrayList<>();
        observers = new ArrayList<>();
    }

    /**
     * Checks if the given list of episodes has unique names.
     *
     * @param episodes the list of episodes to check
     * @return true if the list of episodes has unique names, false otherwise
     */
    private boolean hasUniqueEpisodeName(final ArrayList<Episode> episodes) {
        Set<String> episodeSet = new HashSet<>();

        for (Episode episode : episodes) {
            // Duplicate episode name found
            if (!episodeSet.add(episode.getName())) {
                return false;
            }
        }

        // Episode name is unique
        return true;
    }

    /**
     * Adds a podcast to the host's list of podcasts and the list of all podcasts.
     *
     * @param podcast the podcast to add
     * @return 0 if the podcast is a duplicate, 1 if the podcast has a duplicate episode,
     * 2 if the podcast was successfully added
     */
    public Integer addPodcast(final Podcast podcast) {
        for (Podcast p : podcasts) {
            // Duplicate podcast found
            if (p.getName().equals(podcast.getName())) {
                return 0;
            }
        }

        // Duplicate episode found
        if (!hasUniqueEpisodeName(podcast.getEpisodes())) {
            return 1;
        }

        podcasts.add(podcast);
        allPodcasts.add(podcast);
        return 2;
    }

    /**
     * Adds an announcement to the host's list of announcements and the list of all announcements.
     *
     * @param announcement the announcement to add
     * @return 0 if the announcement is a duplicate, 1 if the announcement was successfully added
     */
    public Integer addAnnouncement(final Announcement announcement) {
        for (Announcement announce : allAnnouncements) {
            // Duplicate announcement found
            if (announce.getName().equals(announcement.getName())
                    && announce.getName().equals(announcement.getName())) {
                return 0;
            }
        }

        announcements.add(announcement);
        allAnnouncements.add(announcement);
        return 1;
    }

    /**
     * Removes an announcement from the host's list of announcements
     * and the list of all announcements.
     *
     * @param announcementName the name of the announcement to remove
     * @return 0 if the announcement was successfully removed, 1 if the announcement was not found
     */
    public Integer removeAnnouncement(final String announcementName) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(announcementName)) {
                announcements.remove(announcement);
                allAnnouncements.remove(announcement);
                return 0;
            }
        }
        return 1;
    }

    /**
     * Removes a podcast from the host's list of podcasts and the list of all podcasts.
     *
     * @param podcast the podcast to remove
     * @return 0 if the podcast was successfully removed, 1 if the podcast has listeners,
     * 2 if the podcast was not found
     */
    public Integer removePodcast(final Podcast podcast) {
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

    public void increaseListeners() {
        listeners++;
    }

    public void addListener(final User user) {
        listenersList.add(user);
    }

    @Override
    public void registerObserver(app.users.user.Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(app.users.user.Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String notification, String description) {
        for (Observer observer : observers) {
            observer.update(notification, description);
        }
    }

    /**
     * Returns the host's host page.
     *
     * @return the host's host page
     */
    public HostPage getHostPage() {
        return hostPage;
    }

    /**
     * Retrurns the array of the host's podcasts.
     *
     * @return the array of the host's podcasts
     */
    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public String getName() {
        return name;
    }

    public Integer getListeners() {
        return listeners;
    }

    public ArrayList<User> getListenersList() {
        return listenersList;
    }
}
