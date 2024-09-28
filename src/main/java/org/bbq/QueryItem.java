package org.bbq;

import org.bbq.lookup.BoundingBoxLookup;

/**
 * This object is able to be used to query a {@link BoundingBoxLookup}.
 */
public sealed interface QueryItem permits BoundingBox, Vec, Line, QueryItem.All {
    QueryItem ALL = All.INSTANCE;

    enum All implements QueryItem {
        INSTANCE
    }
}
