package org.bbq;

import org.bbq.lookup.BoundingBoxLookup;
import org.bbq.lookup.BoundingBoxLookups;

public class Main {
    public static void main(String[] args) {
        BoundingBoxLookup<Double> lookup = BoundingBoxLookups.list();

        BoundingBox boundingBox = new BoundingBox(new Vec(0, 0, 0), new Vec(1, 1, 1));

        lookup.insert(boundingBox, 1.0);

        lookup.visitIntersecting(boundingBox, (bb, val, stop) -> {
            System.out.println(val);
        });
    }
}