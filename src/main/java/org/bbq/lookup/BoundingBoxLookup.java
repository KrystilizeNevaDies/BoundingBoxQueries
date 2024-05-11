package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;

/**
 * Lookup for bounding boxes in 3D space.
 * @param <T> The type of the value associated with the bounding box.
 */
public interface BoundingBoxLookup<T> extends BoundingBoxLookups {

    // Mutability

    /**
     * Inserts a bounding box with the given value.
     * @param boundingBox The bounding box to insert.
     * @param value The value to associate with the bounding box.
     */
    void insert(BoundingBox boundingBox, T value);

    /**
     * Removes the given bounding box and its associated value from the lookup.
     * @param boundingBox The bounding box to remove.
     * @param value The value to remove.
     */
    void remove(BoundingBox boundingBox, T value);

    // Querying

    /**
     * @return The number of bounding box-value pairs in the lookup.
     * @apiNote This method is always O(1).
     */
    double size();

    /**
     * Visits each value in the lookup.
     * @param visitor The visitor to visit the values with.
     * @apiNote This method is always O(n).
     */
    void visitAll(Visitor<T> visitor);

    /**
     * Visits each value in the lookup that intersects with the given query item.
     * @param queryItem The query item to check for intersections.
     * @param visitor The visitor to visit the bounding box with.
     */
    void visitIntersecting(QueryItem queryItem, Visitor<T> visitor);

    interface Visitor<T> {
        /**
         * Visits a bounding box-value pair.
         * @param boundingBox The bounding box.
         * @param value The value.
         * @param stop A runnable that, when run, stops any more visits.
         */
        void visit(BoundingBox boundingBox, T value, Runnable stop);
    }
}
