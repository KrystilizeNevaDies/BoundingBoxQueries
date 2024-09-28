package org.bbq.collection;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;
import org.bbq.lookup.BoundingBoxLookup;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public interface BoundingBoxCollection<T> extends Iterable<T> {

    // Mutability

    /**
     * Inserts a value with an associated bounding box.
     * @param value The value to insert.
     * @param boundingBox The bounding box to associate with the value.
     */
    void insert(T value, BoundingBox boundingBox);

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

    record Entry<T>(T value, BoundingBox boundingBox) {
    }

    /**
     * Visits each value in the lookup that intersects with the given query item.
     * Use {@link QueryItem#ALL} to visit all values.
     * @param queryItem The query item to check for intersections.
     * @implSpec Elements will not be returned more than once.
     */
    @NotNull
    Iterable<BoundingBoxLookup.Entry<T>> visit(QueryItem queryItem);

    @Override
    default @NotNull Iterator<T> iterator() {
        return StreamSupport.stream(visit(QueryItem.ALL).spliterator(), false)
                .map(BoundingBoxLookup.Entry::value)
                .iterator();
    }
}
