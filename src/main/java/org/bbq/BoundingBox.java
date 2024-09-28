package org.bbq;

import org.jetbrains.annotations.Nullable;

public record BoundingBox(Vec min, Vec max) implements QueryItem {

    public static final BoundingBox EMPTY = new BoundingBox(Vec.ZERO, Vec.ZERO);

    /**
     * Constructs the smallest possible bounding box where it's {@link BoundingBox#contains(QueryItem)} passes for
     * both this and the other bounding box.
     * @param other The other bounding box to union with.
     * @return The union of this and the other bounding box.
     */
    public BoundingBox union(BoundingBox other) {
        return new BoundingBox(
            new Vec(Math.min(min.x(), other.min.x()), Math.min(min.y(), other.min.y()), Math.min(min.z(), other.min.z())),
            new Vec(Math.max(max.x(), other.max.x()), Math.max(max.y(), other.max.y()), Math.max(max.z(), other.max.z()))
        );
    }

    /**
     * Constructs the largest possible bounding box using the overlapping area of this and the other bounding box.
     * @param other The other bounding box to intersect with.
     * @return The intersection of this and the other bounding box.
     */
    public BoundingBox intersection(BoundingBox other) {
        Vec min = new Vec(Math.max(this.min.x(), other.min.x()), Math.max(this.min.y(), other.min.y()), Math.max(this.min.z(), other.min.z()));
        Vec max = new Vec(Math.min(this.max.x(), other.max.x()), Math.min(this.max.y(), other.max.y()), Math.min(this.max.z(), other.max.z()));
        return new BoundingBox(min, max);
    }

    public Vec centroid() {
        return min.add(max).mul(0.5);
    }

    public double centroid(int axis) {
        return switch (axis) {
            case 0 -> centroid().x();
            case 1 -> centroid().y();
            case 2 -> centroid().z();
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
        };
    }

    /**
     * @param item The item to check for intersection with.
     * @return {@code true} if the item intersects with this bounding box, {@code false} otherwise.
     */
    public boolean intersects(QueryItem item) {
        return switch (item) {
            case BoundingBox boundingBox -> intersectsBB(boundingBox);
            case Vec vec -> intersectsVec(vec);
            case Line line -> intersectsLine(line);
            case All ignored -> true;
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

    private @Nullable Vec getLineIntersection(double fDst1, double fDst2, Vec P1, Vec P2) {
        if ((fDst1 * fDst2) >= 0.0f) return null;
        if (fDst1 == fDst2) return null;
        return P1.add(P2.sub(P1).mul(-fDst1 / (fDst2 - fDst1)));
    }

    private boolean intersectsLine(Line line) {
        Vec start = line.start();
        Vec end = line.end();

        if (end.x() < min.x() && start.x() < min.x()) return false;
        if (end.x() > max.x() && start.x() > max.x()) return false;
        if (end.y() < min.y() && start.y() < min.y()) return false;
        if (end.y() > max.y() && start.y() > max.y()) return false;
        if (end.z() < min.z() && start.z() < min.z()) return false;
        if (end.z() > max.z() && start.z() > max.z()) return false;
        if (start.x() > min.x() && start.x() < max.x() &&
            start.y() > min.y() && start.y() < max.y() &&
            start.z() > min.z() && start.z() < max.z()) {
            return true;
        }

        {
            Vec hit = getLineIntersection(start.x() - min.x(), end.x() - min.x(), start, end);
            if (hit != null && intersectsVec(hit)) return true;
        }
        {
            Vec hit = getLineIntersection(start.y() - min.y(), end.y() - min.y(), start, end);
            if (hit != null && intersectsVec(hit)) return true;
        }
        {
            Vec hit = getLineIntersection(start.z() - min.z(), end.z() - min.z(), start, end);
            if (hit != null && intersectsVec(hit)) return true;
        }
        {
            Vec hit = getLineIntersection(start.x() - max.x(), end.x() - max.x(), start, end);
            if (hit != null && intersectsVec(hit)) return true;
        }
        {
            Vec hit = getLineIntersection(start.y() - max.y(), end.y() - max.y(), start, end);
            if (hit != null && intersectsVec(hit)) return true;
        }
        {
            Vec hit = getLineIntersection(start.z() - max.z(), end.z() - max.z(), start, end);
            if (hit != null && intersectsVec(hit)) return true;
        }

        return false;
    }

    public boolean contains(QueryItem item) {
        return switch (item) {
            case BoundingBox boundingBox -> containsBB(boundingBox);
            case Vec vec -> containsVec(vec);
            case Line line -> containsLine(line);
            case All ignored -> true;
        };
    }

    private boolean containsBB(BoundingBox other) {
        return min.x() <= other.min.x() && max.x() >= other.max.x() &&
               min.y() <= other.min.y() && max.y() >= other.max.y() &&
               min.z() <= other.min.z() && max.z() >= other.max.z();
    }

    private boolean containsVec(Vec other) {
        return min.x() <= other.x() && max.x() >= other.x() &&
               min.y() <= other.y() && max.y() >= other.y() &&
               min.z() <= other.z() && max.z() >= other.z();
    }

    private boolean containsLine(Line line) {
        return containsVec(line.start()) && containsVec(line.end());
    }

    public Vec center() {
        return new Vec((min.x() + max.x()) * 0.5, (min.y() + max.y()) * 0.5, (min.z() + max.z()) * 0.5);
    }

    public Vec size() {
        return max.sub(min);
    }

    public double surfaceArea() {
        Vec size = size();
        return 2 * (size.x() * size.y() + size.x() * size.z() + size.y() * size.z());
    }
}
