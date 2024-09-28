package org.bbq.wrapper;

import org.bbq.lookup.BoundingBoxLookup;

public interface MinestomBoundingBoxLookups {

    /**
     * Wraps a {@link BoundingBoxLookup} into a {@link MinestomBoundingBoxLookup}.
     * @param lookup the lookup to wrap
     * @return the wrapped lookup
     * @param <T> the type of the lookup
     */
    static <T> MinestomBoundingBoxLookup<T> wrap(BoundingBoxLookup<T> lookup) {
        return new MinestomBoundingBoxLookupImpl<>(lookup);
    }

}
