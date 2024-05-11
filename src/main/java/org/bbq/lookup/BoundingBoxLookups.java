package org.bbq.lookup;

import org.bbq.BoundingBox;

public interface BoundingBoxLookups {

    /**
     * Creates a new {@link BoundingBoxLookup}. This type of implementation is generally faster for very small datasets.
     * <p>
     *   {@link BoundingBoxLookup#insert(BoundingBox, Object)} - O(1)
     *   {@link BoundingBoxLookup#remove(BoundingBox, Object)} - O(n)
     *   {@link BoundingBoxLookup#visitIntersecting(BoundingBox, BoundingBoxLookup.Visitor)} - O(n)
     * </p>
     *
     * @param <T> The type of the value associated with the bounding box.
     * @return A new {@link BoundingBoxLookup} backed by a list.
     */
    static <T> BoundingBoxLookup<T> list() {
        return new ListLookupImpl<>();
    }
}
