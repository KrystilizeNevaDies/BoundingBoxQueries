package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;
import org.bbq.Vec;
import org.bbq.util.IntCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

/**
 * A lookup implementation with a fixed size grid.
 */
class GridLookupImpl<T> implements BoundingBoxLookup<T> {

    /**
     * Default grid width and height
     */
    private static final int DEFAULT_GRID_SIZE = Integer.parseInt(System.getProperty("grid.size", "16"));

    private BoundingBox bounds = BoundingBox.EMPTY;

    private final int gridSize;
    private final List<Entry<T>>[] entries;
    private final Map<Entry<T>, Integer> allEntries = new HashMap<>();

    private final IntCache size = new IntCache(() -> allEntries.values().stream().mapToInt(Integer::intValue).sum());

    GridLookupImpl() {
        this(DEFAULT_GRID_SIZE);
    }

    GridLookupImpl(int gridSize) {
        this.gridSize = gridSize;
        //noinspection unchecked
        this.entries = new List[gridSize * gridSize * gridSize];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new CopyOnWriteArrayList<>();
        }
    }

    private void tryExpand(BoundingBox boundingBox) {
        if (this.bounds.contains(boundingBox)) {
            // no need to expand
            return;
        }

        for (List<Entry<T>> list : entries) {
            list.clear();
        }
        Map<Entry<T>, Integer> valuesCopy = Map.copyOf(allEntries);
        allEntries.clear();

        bounds = bounds.union(boundingBox);

        for (var entry : valuesCopy.entrySet()) {
            Entry<T> entryKey = entry.getKey();
            for (int i = 0; i < entry.getValue(); i++) {
                this.insertUnchecked(entryKey);
            }
        }
    }

    @Override
    public void insert(T value, BoundingBox boundingBox) {
        size.invalidate();
        tryExpand(boundingBox);

        insertUnchecked(new Entry<>(value, boundingBox));
    }

    private void insertUnchecked(Entry<T> entry) {
        allEntries.putIfAbsent(entry, 0);
        allEntries.computeIfPresent(entry, (ignored, integer) -> integer + 1);

        forGridIndicesBB(entry.boundingBox()).forEach(index -> {
            entries[index].add(entry);
        });
    }

    public void remove(T value, BoundingBox boundingBox) {
        size.invalidate();
        Entry<T> entry = new Entry<>(value, boundingBox);
        if (!allEntries.containsKey(entry)) {
            return;
        }

        allEntries.computeIfPresent(entry, (ignored, integer) -> integer - 1);
        if (allEntries.get(entry) == 0) {
            allEntries.remove(entry);
        }

        forGridIndicesBB(boundingBox).forEach(index -> {
            entries[index].remove(entry);
        });
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public @NotNull Iterable<BoundingBoxLookup.Entry<T>> visit(QueryItem queryItem) {
        Map<Entry<T>, Boolean> visited = new IdentityHashMap<>();
        var iterator = forGridInstances(queryItem).iterator();

        Map<Entry<T>, Boolean> unused = new IdentityHashMap<>();

        return () -> new Iterator<>() {

            @Override
            public boolean hasNext() {
                // read until iterator is empty or unused has a new entry
                while (iterator.hasNext() && unused.isEmpty()) {
                    int index = iterator.nextInt();
                    if (index < 0 || index >= entries.length) {
                        // out of bounds
                        continue;
                    }
                    for (Entry<T> entry : entries[index]) {
                        if (visited.containsKey(entry)) {
                            continue;
                        }
                        if (!entry.boundingBox().intersects(queryItem)) {
                            continue;
                        }
                        unused.put(entry, true);
                        visited.put(entry, true);
                    }
                }

                return !unused.isEmpty();
            }

            @Override
            public BoundingBoxLookup.Entry<T> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }

                Entry<T> next = unused.keySet().iterator().next();
                unused.remove(next);
                return next;
            }
        };
    }

    private IntStream forGridInstances(QueryItem queryItem) {
        return switch (queryItem) {
            case BoundingBox boundingBox -> forGridIndicesBB(boundingBox);
            case Vec vec -> forGridIndicesVec(vec);
            case QueryItem.All ignored -> forGridIndicesAll();
            default -> throw new IllegalStateException("Unexpected value: " + queryItem);
        };
    }

    private IntStream forGridIndicesBB(BoundingBox boundingBox) {

        Vec boundsMin = bounds.min();
        Vec boundsMax = bounds.max();

        Vec size = boundsMax.sub(boundsMin);

        double gridSize = this.gridSize;

        Vec min = boundingBox.min();
        Vec max = boundingBox.max();

        Vec relMin = min.sub(boundsMin);
        Vec relMax = max.sub(boundsMin);

        // normalize to grid size
        Vec normMin = relMin.div(size).mul(gridSize);
        Vec normMax = relMax.div(size).mul(gridSize);

        int minX = (int) Math.floor(normMin.x());
        int minY = (int) Math.floor(normMin.y());
        int minZ = (int) Math.floor(normMin.z());

        int maxX = (int) Math.ceil(normMax.x());
        int maxY = (int) Math.ceil(normMax.y());
        int maxZ = (int) Math.ceil(normMax.z());

//        for (int x = minX; x < maxX; x++) {
//            for (int y = minY; y < maxY; y++) {
//                for (int z = minZ; z < maxZ; z++) {
//                    int index = x + y * this.gridSize + z * this.gridSize * this.gridSize;
//                    if (consumer.test(index)) {
//                        return;
//                    }
//                }
//            }
//        }

        IntSupplier generator = new IntSupplier() {

            int x = minX;
            int y = minY;
            int z = minZ;

            @Override
            public int getAsInt() {
                int index = (int) (x + y * gridSize + z * gridSize * gridSize);

                z++;
                if (z >= maxZ) {
                    z = minZ;
                    y++;
                    if (y >= maxY) {
                        y = minY;
                        x++;
                        if (x > maxX) {
                            return -1;
                        }
                    }
                }

                return index;
            }
        };

        return IntStream.generate(generator).takeWhile(i -> i != -1);
    }

    private IntStream forGridIndicesVec(Vec vec) {
        Vec boundsMin = bounds.min();
        Vec boundsMax = bounds.max();

        Vec size = boundsMax.sub(boundsMin);

        double gridSize = this.gridSize;

        Vec rel = vec.sub(boundsMin);

        // normalize to grid size
        Vec norm = rel.div(size).mul(gridSize);

        int x = (int) Math.floor(norm.x());
        int y = (int) Math.floor(norm.y());
        int z = (int) Math.floor(norm.z());

        int index = x + y * this.gridSize + z * this.gridSize * this.gridSize;
        return IntStream.of(index);
    }

    private IntStream forGridIndicesAll() {
        return IntStream.range(0, entries.length);
    }
}
