package org.bbq.wrapper;

import net.minestom.server.collision.BoundingBox;

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
public interface MinestomBoundingBoxLookup<T> extends WrapperBoundingBoxLookup<T, BoundingBox, MinestomQueryItem> {

    /** {@inheritDoc} */
    @Override
    default void remove(T value) {
        List<Entry<T, BoundingBox>> boundingBoxes = new ArrayList<>();
        for (Entry<T, BoundingBox> entry : visit(MinestomQueryItem.ALL)) {
            if (Objects.equals(entry.value(), value)) {
                boundingBoxes.add(entry);
            }
        }
        for (Entry<T, BoundingBox> entry : boundingBoxes) {
            remove(value, entry.boundingBox());
        }
    }
}
