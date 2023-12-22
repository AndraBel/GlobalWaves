package app.userPages;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PageAccept {
    /**
     * Common method for accepting a visitor
     * @param visitor The visitor to be accepted
     * @param resultNode The node to be updated with the content
     */
    void accept(PageVisitor visitor, ObjectNode resultNode);

    /**
     * Common method for getting the content of a page
     * @param resultNode The node to be updated with the content
     */
    void getContent(ObjectNode resultNode);
}
