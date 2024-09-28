package org.bbq.wrapper;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public interface WrapperBoundingBoxCollection<T, B, Q> extends Iterable<T> {

    // Mutability

    /**
     * Inserts a value with an associated bounding box.
     * @param value The value to insert.
     * @param boundingBox The bounding box to associate with the value.
     */
    void insert(T value, B boundingBox);

    /**
     * Removes all values where {@link java.util.Objects#equals(Object, Object)} returns true for the given value.
     * @param value The value to remove.
     */
    void remove(T value);

    // Querying

    /**
     * @return The number of values (or entries) in the collection.
     */
    int size();

    record Entry<T, B>(T value, B boundingBox) {
    }

    /**
     * Visits each value in the lookup that intersects with the given query item.
     * @param queryItem The query item to check for intersections.
     * @implSpec Elements will not be returned more than once.
     */
    @NotNull
    Iterable<Entry<T, B>> visit(Q queryItem);
}
