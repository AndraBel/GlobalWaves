package app.audioFiles;


import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class representing an audio file
 */
public class AudioFiles {
    protected String name;
    protected String owner;
    protected Integer listeners;

    /**
     * @param filters HashMap of filters to be applied
     * @return true if the audio file matches all the filters, false otherwise
     */
    public boolean matchFilters(final HashMap<String, Object> filters) {
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            switch (filter.getKey()) {
                case "name":
                    if (!name.toLowerCase()
                            .startsWith(((String) filter.getValue()).toLowerCase())) {
                        return false;
                    }
                    break;
                case "owner":
                    if (!owner.equalsIgnoreCase((String) filter.getValue())) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * Increases the listener count by one, meaning a user is listening to the audio file selected.
     */
    public void increaseListeners() {
        listeners++;
    }

    /**
     * Decreases the listener count by one, meaning a user has stopped
     * listening to the audio file selected.
     */
    public void decreaseListeners() {
        listeners--;
    }

    /**
     * Retrieves the name associated with the audio file.
     *
     * @return A String representing the name of the audio file.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the owner associated with the audio file.
     *
     * @return A String representing the owner of the audio file.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return the number of listeners of the audio file.
     */
    public Integer getListeners() {
        return listeners;
    }
}
