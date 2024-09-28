package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;

public interface BoundingBoxLookups {

    /**
     * Creates a new {@link BoundingBoxLookup}. This type of implementation is generally faster for very small datasets.
     * <p>
     *   {@link BoundingBoxLookup#insert(BoundingBox, Object)} - O(1)
     *   {@link BoundingBoxLookup#remove(BoundingBox, Object)} - O(n)
     *   {@link BoundingBoxLookup#visit(QueryItem)} - O(n)
     * </p>
     * @implNote This implementation is backed by {@link java.util.List}
     */
    static <T> BoundingBoxLookup<T> list() {
        return new ListLookupImpl<>();
    }

    /**
     * Creates a new {@link BoundingBoxLookup}. This type of implementation is generally faster for medium datasets.
     * <p>
     *     {@link BoundingBoxLookup#insert(BoundingBox, Object)} - O(1)
     *     {@link BoundingBoxLookup#remove(BoundingBox, Object)} - O(log(n))
     *     {@link BoundingBoxLookup#visit(QueryItem)} - O(log(n))
     * <p>
     */
    static <T> BoundingBoxLookup<T> grid() {
        return new GridLookupImpl<>();
    }

    /**
     * Creates a new {@link BoundingBoxLookup}. This type of implementation is generally faster for large datasets.
     * <p>
     *     {@link BoundingBoxLookup#insert(BoundingBox, Object)} - O(log(n))
     *     {@link BoundingBoxLookup#remove(BoundingBox, Object)} - O(log(n))
     *     {@link BoundingBoxLookup#visit(QueryItem)} - O(log(n))
     * <p>
     */
    static <T> BoundingBoxLookup<T> tree() {
        return new TreeLookupImpl<>();
    }
}
