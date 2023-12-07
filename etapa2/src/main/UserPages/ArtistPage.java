package main.UserPages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Album;
import main.Event;
import main.Merch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ArtistPage extends Page {
    private LinkedHashMap<String, Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merch;
    private Integer listeners;


    public ArtistPage(final LinkedHashMap<String, Album> albums,
                      final ArrayList<Event> events,
                      final ArrayList<Merch> merch) {
        super(null, null);
        this.albums = albums;
        this.events = events;
        this.merch = merch;
        listeners = 0;
    }

    @Override
    public void getContent(ObjectNode resultNode) {
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

    public void increaseListeners() {
        listeners++;
    }
    public void decreaseListeners() {
        listeners--;
    }

    public Integer getListeners() {
        return listeners;
    }
}
