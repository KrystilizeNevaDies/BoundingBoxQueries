package org.bbq.wrapper;

import org.bbq.BoundingBox;

public interface BoundingBoxWrapper<T> {
    BoundingBox toBoundingBox(T t);
    T fromBoundingBox(BoundingBox boundingBox);
}
