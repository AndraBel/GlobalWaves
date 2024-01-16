package app.users.artist;

public class ArtistRevenue {
    private final Artist artist;
    private final double totalRevenue;

    public ArtistRevenue(final Artist artist, final double totalRevenue) {
        this.artist = artist;
        this.totalRevenue = totalRevenue;
    }

    /**
     * @return the artist
     */
    public Artist getArtist() {
        return artist;
    }
}
