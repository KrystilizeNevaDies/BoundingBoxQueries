package org.bbq.wrapper;

import net.minestom.server.collision.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public interface MinestomBoundingBoxCollection<T> extends WrapperBoundingBoxCollection<T, BoundingBox, MinestomQueryItem> {

    @Override
    default @NotNull Iterator<T> iterator() {
        return StreamSupport.stream(visit(MinestomQueryItem.ALL).spliterator(), false)
                .map(WrapperBoundingBoxLookup.Entry::value)
                .iterator();
    }
}
