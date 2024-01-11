package app.users.arist;

import app.users.arist.Artist;

public class ArtistRevenue {
    private final Artist artist;
    private final double totalRevenue;

    public ArtistRevenue(Artist artist, double totalRevenue) {
        this.artist = artist;
        this.totalRevenue = totalRevenue;
    }

    public Artist getArtist() {
        return artist;
    }
}
