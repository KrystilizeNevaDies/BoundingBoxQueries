package org.bbq;

import org.jetbrains.annotations.NotNull;

public record BoundingBox(Vec min, Vec max) implements QueryItem {

    public BoundingBox union(BoundingBox other) {
        return new BoundingBox(
            new Vec(Math.min(min.x(), other.min.x()), Math.min(min.y(), other.min.y()), Math.min(min.z(), other.min.z())),
            new Vec(Math.max(max.x(), other.max.x()), Math.max(max.y(), other.max.y()), Math.max(max.z(), other.max.z()))
        );
    }

    public BoundingBox intersection(BoundingBox other) {
        Vec min = new Vec(Math.max(this.min.x(), other.min.x()), Math.max(this.min.y(), other.min.y()), Math.max(this.min.z(), other.min.z()));
        Vec max = new Vec(Math.min(this.max.x(), other.max.x()), Math.min(this.max.y(), other.max.y()), Math.min(this.max.z(), other.max.z()));
        return new BoundingBox(min, max);
    }

    public boolean intersects(QueryItem item) {
        return switch (item) {
            case BoundingBox boundingBox -> intersectsBB(boundingBox);
            case Vec vec -> intersectsVec(vec);
        };
    }

    private boolean intersectsBB(BoundingBox other) {
        return min.x() <= other.max.x() && max.x() >= other.min.x() &&
                min.y() <= other.max.y() && max.y() >= other.min.y() &&
                min.z() <= other.max.z() && max.z() >= other.min.z();
    }

    private boolean intersectsVec(Vec other) {
        return min.x() <= other.x() && max.x() >= other.x() &&
                min.y() <= other.y() && max.y() >= other.y() &&
                min.z() <= other.z() && max.z() >= other.z();
    }

    public boolean contains(BoundingBox other) {
        return min.x() <= other.min.x() && max.x() >= other.max.x() &&
               min.y() <= other.min.y() && max.y() >= other.max.y() &&
               min.z() <= other.min.z() && max.z() >= other.max.z();
    }
}
