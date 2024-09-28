package org.bbq;

import org.bbq.lookup.BoundingBoxLookup;
import org.bbq.lookup.BoundingBoxLookups;

public class Main {
    public static void main(String[] args) {
        BoundingBoxLookup<Double> lookup = BoundingBoxLookups.tree();

        BoundingBox boundingBox1 = new BoundingBox(new Vec(0, 0, 0), new Vec(1, 1, 1));
        lookup.insert(1.0, boundingBox1);
        BoundingBox boundingBox = new BoundingBox(new Vec(-35, -53, -1), new Vec(52, 43, 16));
        lookup.insert(2.0, boundingBox);

        for (BoundingBoxLookup.Entry<Double> entry : lookup.visit(QueryItem.ALL)) {
            System.out.println(entry);
        }
    }
}
