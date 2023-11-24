package main;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class representing an audio file
 */
public class AudioFiles {
    protected String name;
    protected String owner;

    /**
     * @param filters HashMap of filters to be applied
     * @return true if the audio file matches all the filters, false otherwise
     */
    public boolean matchFilters(final HashMap<String, Object> filters) {
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            switch (filter.getKey()) {
                case "name":
                    if (!name.startsWith((String) filter.getValue())) {
                        return false;
                    }
                    break;
                case "owner":
                    if (!owner.equals((String) filter.getValue())) {
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

}
