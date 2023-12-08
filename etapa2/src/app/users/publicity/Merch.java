package app.users.publicity;

public class Merch extends Publicity {
    private final Integer price;

    public Merch(final String owner, final String name,
                 final String description, final Integer price) {
        super(owner, name, description);
        this.price = price;
    }

    /**
     * @return the price of the merch
     */
    public Integer getPrice() {
        return price;
    }
}
