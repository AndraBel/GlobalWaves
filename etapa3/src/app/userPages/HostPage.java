package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.podcasts.Episode;
import app.audioFiles.podcasts.Podcast;
import app.users.userComponents.publicity.Announcement;

import java.util.ArrayList;
import java.util.List;

public class HostPage extends Page {
    private String name;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
    private Integer listeners;

    public HostPage(final ArrayList<Podcast> podcasts,
                    final ArrayList<Announcement> announcements,
                    final String name) {
        super(null, null);
        this.podcasts = podcasts;
        this.announcements = announcements;
        listeners = 0;
        this.name = name;
    }

    /**
     * Accepts a visitor and calls the visit method on it
     */
    @Override
    public void accept(final PageVisitor visitor, final ObjectNode resultNode) {
        visitor.visit(this, resultNode);
    }

    /**
     * Updates the resultNode with the content of the page
     *
     * @param resultNode The node to be updated with the content
     */
    @Override
    public void getContent(final ObjectNode resultNode) {
        List<String> podcastDetails = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            List<String> episodeDetails = new ArrayList<>();
            for (Episode episode : podcast.getEpisodes()) {
                String episodeInfo = String.format("%s - %s", episode.getName(),
                                                    episode.getDescription());
                episodeDetails.add(episodeInfo);
            }

            String podcastInfo = String.format("%s:\n\t[%s]\n", podcast.getName(),
                                                String.join(", ", episodeDetails));
            podcastDetails.add(podcastInfo);
        }

        List<String> announcementDetails = new ArrayList<>();
        if (announcements == null) {
            announcementDetails.add("[]");
        } else {
            for (Announcement announcement : announcements) {
                String announcementInfo = String.format("%s:\n\t%s\n", announcement.getName(),
                                                        announcement.getDescription());
                announcementDetails.add(announcementInfo);
            }
        }

        resultNode.put("message",
                    String.format("Podcasts:\n\t[%s]\n\nAnnouncements:\n\t[%s]",
                    String.join(", ", podcastDetails),
                    String.join(", ", announcementDetails)));

    }

    /**
     * Common method for increasing the number of listeners
     */
    public void increaseListeners() {
        listeners++;
    }

    /**
     * Common method for decreasing the number of listeners
     */
    public void decreaseListeners() {
        listeners--;
    }

    /**
     * Retrieves the number of listeners
     *
     * @return An Integer representing the number of listeners
     */
    public Integer getListeners() {
        return listeners;
    }

    /**
     * Retrieves the podcasts
     *
     * @return An ArrayList representing the podcasts
     */
    public ArrayList<Podcast> getPodcasts() {
        return podcasts;
    }

    public String getName() {
        return name;
    }
}
