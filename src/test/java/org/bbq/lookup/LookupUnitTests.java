package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;
import org.bbq.Vec;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LookupUnitTests {

    @Test
    public void testSingleElementInsertionDeletion() {
        for (BoundingBoxLookup<String> lookup : generateLookups()) {
            BoundingBox boundingBox = new BoundingBox(new Vec(0, 0, 0), new Vec(1, 1, 1));

            BoundingBox inside = new BoundingBox(new Vec(0.5, 0.5, 0.5), new Vec(0.6, 0.6, 0.6));
            BoundingBox outside = new BoundingBox(new Vec(2, 2, 2), new Vec(3, 3, 3));

            // insert
            lookup.insert("1.0", boundingBox);
            List<String> result = visitList(lookup, boundingBox);

            assertEquals(1, result.size());
            assertEquals("1.0", result.getFirst());

            // query
            result = visitList(lookup, inside);

            assertEquals(1, result.size());
            assertEquals("1.0", result.getFirst());

            result = visitList(lookup, outside);

            assertEquals(0, result.size());

            // remove
            lookup.remove("1.0", boundingBox);

            result = visitList(lookup, boundingBox);

            assertEquals(0, result.size());
        }
    }

    @Test
    public void randomElementInsertionTests() {
        Collection<BoundingBoxLookup<String>> lookups = generateLookups();
        Random random = new Random(42);

        // insert random bounding boxes
        for (int i = 0; i < 10000; i++) {
            double minX = random.nextDouble(0, 100);
            double minY = random.nextDouble(0, 100);
            double minZ = random.nextDouble(0, 100);

            double maxX = random.nextDouble(minX, 100);
            double maxY = random.nextDouble(minY, 100);
            double maxZ = random.nextDouble(minZ, 100);

            BoundingBox boundingBox = new BoundingBox(new Vec(minX, minY, minZ), new Vec(maxX, maxY, maxZ));
            String randomValue = new UUID(random.nextLong(), random.nextLong()).toString();

            for (var lookup : lookups) {
                lookup.insert(randomValue, boundingBox);
                assertEquals(i + 1, lookup.size());
            }
        }

        assertLookupsContentEquals(lookups);
    }

    private Collection<BoundingBoxLookup<String>> generateLookups() {
        return List.of(
                BoundingBoxLookups.list(),
                BoundingBoxLookups.grid(),
                BoundingBoxLookups.tree()
        );
    }

    private <T> List<T> visitList(BoundingBoxLookup<T> lookup, QueryItem query) {
        return visitEntriesList(lookup, query).stream().map(BoundingBoxLookup.Entry::value).toList();
    }

    private <T> List<BoundingBoxLookup.Entry<T>> visitEntriesList(BoundingBoxLookup<T> lookup, QueryItem query) {
        List<BoundingBoxLookup.Entry<T>> result = new ArrayList<>();
        for (BoundingBoxLookup.Entry<T> entry : lookup.visit(query)) {
            result.add(entry);
        }
        return result;
    }

    private void assertLookupsContentEquals(Collection<BoundingBoxLookup<String>> lookups) {
        for (var lookupA : lookups) {
            for (var lookupB : lookups) {
                if (lookupA == lookupB) {
                    continue;
                }
                Set<BoundingBoxLookup.Entry<String>> listA = Set.copyOf(visitEntriesList(lookupA, QueryItem.ALL));
                Set<BoundingBoxLookup.Entry<String>> listB = Set.copyOf(visitEntriesList(lookupB, QueryItem.ALL));

                Set<BoundingBoxLookup.Entry<String>> diff = new HashSet<>(listA);
                diff.removeAll(listB);

                assertEquals(listA, listB);
            }
        }
    }
}
