package org.bbq.wrapper;

import org.bbq.lookup.BoundingBoxLookup;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.stream.StreamSupport;

class WrapperBoundingBoxLookupImpl<T, B, Q> implements WrapperBoundingBoxLookup<T, B, Q> {

    private final BoundingBoxLookup<T> lookup;
    private final BoundingBoxWrapper<B> boundingBoxWrapper;
    private final QueryItemWrapper<Q> queryItemWrapper;

    public WrapperBoundingBoxLookupImpl(BoundingBoxLookup<T> lookup, BoundingBoxWrapper<B> boundingBoxWrapper, QueryItemWrapper<Q> queryItemWrapper) {
        this.lookup = lookup;
        this.boundingBoxWrapper = boundingBoxWrapper;
        this.queryItemWrapper = queryItemWrapper;
    }

    @Override
    public void remove(T value, B boundingBox) {
        lookup.remove(value, boundingBoxWrapper.toBoundingBox(boundingBox));
    }

    @Override
    public void insert(T value, B boundingBox) {
        lookup.insert(value, boundingBoxWrapper.toBoundingBox(boundingBox));
    }

    @Override
    public void remove(T value) {
        lookup.remove(value);
    }

    @Override
    public int size() {
        return lookup.size();
    }

    @Override
    public @NotNull Iterable<Entry<T, B>> visit(Q queryItem) {
        return () -> StreamSupport.stream(lookup.visit(queryItemWrapper.toQueryItem(queryItem)).spliterator(), false)
                .map(entry -> new Entry<>(entry.value(), boundingBoxWrapper.fromBoundingBox(entry.boundingBox())))
                .iterator();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return lookup.iterator();
    }
}
