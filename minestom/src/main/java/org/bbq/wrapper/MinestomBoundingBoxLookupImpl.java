package org.bbq.wrapper;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import org.bbq.Line;
import org.bbq.QueryItem;
import org.bbq.lookup.BoundingBoxLookup;

class MinestomBoundingBoxLookupImpl<T> extends WrapperBoundingBoxLookupImpl<T, BoundingBox, MinestomQueryItem> implements MinestomBoundingBoxLookup<T> {
    public MinestomBoundingBoxLookupImpl(BoundingBoxLookup<T> lookup) {
        super(lookup, BoundingBoxConverter.INSTANCE, QueryItemConverter.INSTANCE);
    }
}

enum BoundingBoxConverter implements BoundingBoxWrapper<BoundingBox> {
    INSTANCE;

    @Override
    public org.bbq.BoundingBox toBoundingBox(BoundingBox boundingBox) {
        double minX = boundingBox.minX();
        double minY = boundingBox.minY();
        double minZ = boundingBox.minZ();

        double maxX = boundingBox.maxX();
        double maxY = boundingBox.maxY();
        double maxZ = boundingBox.maxZ();

        return new org.bbq.BoundingBox(new org.bbq.Vec(minX, minY, minZ), new org.bbq.Vec(maxX, maxY, maxZ));
    }

    @Override
    public BoundingBox fromBoundingBox(org.bbq.BoundingBox boundingBox) {
        Vec min = VecConverter.INSTANCE.fromVec(boundingBox.min());
        Vec max = VecConverter.INSTANCE.fromVec(boundingBox.max());
        return new BoundingBox(min, max);
    }
}

enum VecConverter {
    INSTANCE;

    public org.bbq.Vec toVec(Vec vec) {
        return new org.bbq.Vec(vec.x(), vec.y(), vec.z());
    }

    public Vec fromVec(org.bbq.Vec vec) {
        return new Vec(vec.x(), vec.y(), vec.z());
    }
}

enum QueryItemConverter implements QueryItemWrapper<MinestomQueryItem> {
    INSTANCE;


    @Override
    public QueryItem toQueryItem(MinestomQueryItem minestomQueryItem) {
        return switch (minestomQueryItem) {
            case MinestomQueryItem.All ignored -> QueryItem.ALL;
            case MinestomQueryItem.BoundingBox boundingBox -> BoundingBoxConverter.INSTANCE.toBoundingBox(boundingBox.boundingBox());
            case MinestomQueryItem.Vec vec -> VecConverter.INSTANCE.toVec(vec.vec());
            case MinestomQueryItem.Line line -> new Line(VecConverter.INSTANCE.toVec(line.start()), VecConverter.INSTANCE.toVec(line.end()));
        };
    }

    @Override
    public MinestomQueryItem fromQueryItem(QueryItem queryItem) {
        return switch (queryItem) {
            case org.bbq.BoundingBox boundingBox -> new MinestomQueryItem.BoundingBox(BoundingBoxConverter.INSTANCE.fromBoundingBox(boundingBox));
            case org.bbq.Vec vec -> new MinestomQueryItem.Vec(VecConverter.INSTANCE.fromVec(vec));
            case Line line -> new MinestomQueryItem.Line(VecConverter.INSTANCE.fromVec(line.start()), VecConverter.INSTANCE.fromVec(line.end()));
            case QueryItem.All ignored -> MinestomQueryItem.ALL;
        };
    }
}
