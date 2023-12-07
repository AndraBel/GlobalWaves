package main;

public class Merch {
    private final String owner;
    private final String name;
    private final String description;
    private final Integer price;

    public Merch(final String owner, final String name,
                 final String description, final Integer price) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
