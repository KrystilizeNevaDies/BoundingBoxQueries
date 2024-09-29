package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;
import org.bbq.util.IntCache;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A lookup implementation with an {@link ArrayList} backing the lookup.
 */
class ListLookupImpl<T> implements BoundingBoxLookup<T> {

    private final ArrayList<Entry<T>> entries = new ArrayList<>();

    ListLookupImpl() {
    }

    public void insert(T value, BoundingBox boundingBox) {
        entries.add(new Entry<>(value, boundingBox));
    }

    @Override
    public void remove(T value, BoundingBox boundingBox) {
        entries.remove(new Entry<>(value, boundingBox));
    }

    @Override
    public @NotNull Iterable<Entry<T>> visit(QueryItem queryItem) {
        return () -> entries.stream()
                .filter(entry -> entry.boundingBox().intersects(queryItem))
                .iterator();
    }

    @Override
    public int size() {
        return entries.size();
    }
}
