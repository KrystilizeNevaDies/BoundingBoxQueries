package org.bbq.wrapper;

import org.bbq.QueryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lookup for 3D BoundingBox-Value pairs.
 * <p>
 *     Neither the values nor the bounding boxes, nor the pair as a whole is unique.
 *     you may insert as many entries with the same value and bounding box as you like, the size of the collection will
 *     still increase.<br><br>
 *     For example, If I insert any entry (lets call it "A") two times, then remove it once, the collection will still
 *     contain one entry ("A").
 * </p>
 *
 * @param <T> The type of the value associated with a bounding box.
 */
public interface WrapperBoundingBoxLookup<T, B, Q> extends WrapperBoundingBoxCollection<T, B, Q> {

    /**
     * Removes the given bounding box and its associated value from the lookup.
     * @param value The value to remove.
     * @param boundingBox The bounding box to remove.
     */
    void remove(T value, B boundingBox);
}
