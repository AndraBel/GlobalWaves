package app;

import app.users.Artist;

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

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
