package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import app.audioFiles.audioCollection.Album;
import app.users.userComponents.publicity.Event;
import app.users.userComponents.publicity.Merch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ArtistPage extends Page implements PageAccept {
    private String name;
    private LinkedHashMap<String, Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merch;
    private Integer listeners;
    private double merchRevenue;

    public ArtistPage(final LinkedHashMap<String, Album> albums,
                      final ArrayList<Event> events,
                      final ArrayList<Merch> merch,
                      final String name) {
        super(null, null);
        this.albums = albums;
        this.events = events;
        this.merch = merch;
        listeners = 0;
        this.name = name;
        merchRevenue = 0;
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
     * @param resultNode The node to be updated with the content
     */
    @Override
    public void getContent(final ObjectNode resultNode) {
        List<String> albumNames = new ArrayList<>();
        for (Album album : albums.values()) {
            albumNames.add(album.getName());
        }

        List<String> merchDetails = new ArrayList<>();
        for (Merch merchItem : merch) {
            String merchInfo = String.format("%s - %d:\n\t%s",
                    merchItem.getName(), merchItem.getPrice(), merchItem.getDescription());
            merchDetails.add(merchInfo);
        }

        List<String> eventDetails = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Event event : events) {
            String trimmedDate = event.getDate().trim();
            String eventInfo = String.format("%s - %s:\n\t%s", event.getName(),
                    LocalDate.parse(trimmedDate, dateFormatter).format(dateFormatter),
                    event.getDescription());
            eventDetails.add(eventInfo);
        }

        resultNode.put("message", "Albums:\n\t" + albumNames
                + "\n\nMerch:\n\t"
                + merchDetails
                + "\n\nEvents:\n\t"
                + eventDetails);
    }

    /**
     * Common method for decreasing the number of listeners
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

    public void increaseMerchRevenue(final double price) {
        merchRevenue += price;
    }

    /**
     * @return the number of listeners
     */
    public Integer getListeners() {
        return listeners;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Merch> getMerch() {
        return merch;
    }

    public double getMerchRevenue() {
        return merchRevenue;
    }
}
