package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class PagePrinter implements PageVisitor {
    /**
     * Visits a HomePage and prints its content.
     *
     * @param homePage     The HomePage to visit.
     * @param resultNode   The ObjectNode to hold the result.
     * @see HomePage
     */
    @Override
    public void visit(final HomePage homePage, final ObjectNode resultNode) {
        homePage.getContent(resultNode);
    }

    /**
     * Visits a LikedContentPage and prints its content.
     *
     * @param likedContentPage  The LikedContentPage to visit.
     * @param resultNode       The ObjectNode to hold the result.
     * @see LikedContentPage
     */
    @Override
    public void visit(final LikedContentPage likedContentPage, final ObjectNode resultNode) {
        likedContentPage.getContent(resultNode);
    }

    /**
     * Visits an ArtistPage and prints its content.
     *
     * @param artistPage   The ArtistPage to visit.
     * @param resultNode   The ObjectNode to hold the result.
     * @see ArtistPage
     */
    @Override
    public void visit(final ArtistPage artistPage, final ObjectNode resultNode) {
        artistPage.getContent(resultNode);
    }

    /**
     * Visits a HostPage and prints its content.
     *
     * @param hostPage     The HostPage to visit.
     * @param resultNode   The ObjectNode to hold the result.
     * @see HostPage
     */
    @Override
    public void visit(final HostPage hostPage, final ObjectNode resultNode) {
        hostPage.getContent(resultNode);
    }
}
