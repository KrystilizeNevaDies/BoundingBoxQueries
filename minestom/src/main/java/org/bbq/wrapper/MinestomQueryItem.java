package org.bbq.wrapper;

import org.bbq.lookup.BoundingBoxLookup;

/**
 * This object is able to be used to query a {@link BoundingBoxLookup}.
 */
public sealed interface MinestomQueryItem permits MinestomQueryItem.All, MinestomQueryItem.BoundingBox, MinestomQueryItem.Line, MinestomQueryItem.Vec {
    MinestomQueryItem ALL = All.INSTANCE;

    enum All implements MinestomQueryItem {
        INSTANCE
    }

    /**
     * Represents a bounding box.
     */
    record BoundingBox(net.minestom.server.collision.BoundingBox boundingBox) implements MinestomQueryItem { }

    /**
     * Represents a vector.
     */
    record Vec(net.minestom.server.coordinate.Vec vec) implements MinestomQueryItem { }

    /**
     * Represents a line.
     */
    record Line(net.minestom.server.coordinate.Vec start, net.minestom.server.coordinate.Vec end) implements MinestomQueryItem { }
}
