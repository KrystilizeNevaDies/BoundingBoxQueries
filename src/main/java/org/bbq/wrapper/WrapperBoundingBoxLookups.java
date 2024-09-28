package org.bbq.wrapper;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;
import org.bbq.lookup.BoundingBoxLookup;
import org.bbq.lookup.BoundingBoxLookups;

public interface WrapperBoundingBoxLookups {
    /**
     * Wraps a {@link BoundingBoxLookup} with a {@link BoundingBoxWrapper} and a {@link QueryItemWrapper}.
     * @param lookup The lookup to wrap.
     * @param boundingBoxWrapper The bounding box wrapper to use.
     * @param queryItemWrapper The query item wrapper to use.
     * @return A new {@link WrapperBoundingBoxLookup} that wraps the given lookup.
     * @param <T> The type of the value associated with a bounding box.
     * @param <B> The type of the bounding box.
     * @param <Q> The type of the query item.
     */
    static <T, B, Q> WrapperBoundingBoxLookup<T, B, Q> wrap(BoundingBoxLookup<T> lookup, BoundingBoxWrapper<B> boundingBoxWrapper, QueryItemWrapper<Q> queryItemWrapper) {
        return new WrapperBoundingBoxLookupImpl<>(lookup, boundingBoxWrapper, queryItemWrapper);
    }
}
