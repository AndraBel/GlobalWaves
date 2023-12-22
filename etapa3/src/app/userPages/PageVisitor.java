package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PageVisitor {
    /**
     * Visits a HomePage and performs a specific operation
     *
     * @param homePage The HomePage to visit
     * @param resultNode The ObjectNode to hold the result
     */
    void visit(HomePage homePage, ObjectNode resultNode);

    /**
     * Visits a LikedContentPage and performs a specific operation
     *
     * @param likedContentPage The LikedContentPage to visit
     * @param resultNode The ObjectNode to hold the result
     */
    void visit(LikedContentPage likedContentPage, ObjectNode resultNode);

    /**
     * Visits an ArtistPage and performs a specific operation
     *
     * @param artistPage The ArtistPage to visit
     * @param resultNode The ObjectNode to hold the result
     */
    void visit(ArtistPage artistPage, ObjectNode resultNode);

    /**
     * Visits a HostPage and performs a specific operation
     *
     * @param hostPage The HostPage to visit
     * @param resultNode The ObjectNode to hold the result
     */
    void visit(HostPage hostPage, ObjectNode resultNode);
}
