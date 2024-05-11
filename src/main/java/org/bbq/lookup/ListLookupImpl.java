package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A lookup implementation with an {@link java.util.ArrayList} backing the lookup.
 */
class ListLookupImpl<T> implements BoundingBoxLookup<T> {

    private record Entry<T>(BoundingBox boundingBox, T value) {
    }

    private final ArrayList<Entry<T>> entries = new ArrayList<>();

    ListLookupImpl() {
    }

    @Override
    public void insert(BoundingBox boundingBox, T value) {
        entries.add(new Entry<>(boundingBox, value));
    }

    @Override
    public void remove(BoundingBox boundingBox, T value) {
        entries.removeIf(entry -> entry.boundingBox.equals(boundingBox) && Objects.equals(entry.value, value));
    }

    @Override
    public void visitAll(Visitor<T> visitor) {
        AtomicBoolean stop = new AtomicBoolean(false);
        for (Entry<T> entry : entries) {
            visitor.visit(entry.boundingBox, entry.value, () -> stop.set(true));
            if (stop.get()) {
                return;
            }
        }
    }

    @Override
    public void visitIntersecting(QueryItem queryItem, Visitor<T> visitor) {
        visitAll((bb, val, stop) -> {
            if (bb.intersects(queryItem)) {
                visitor.visit(bb, val, stop);
            }
        });
    }

    @Override
    public double size() {
        return entries.size();
    }
}
