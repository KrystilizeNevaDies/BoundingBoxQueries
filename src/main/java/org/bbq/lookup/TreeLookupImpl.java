package org.bbq.lookup;

import org.bbq.BoundingBox;
import org.bbq.QueryItem;
import org.bbq.util.IntCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeLookupImpl<T> implements BoundingBoxLookup<T> {

    // Internal node class for BVH
    private sealed interface Node<T> permits Leaf, Branch {
        BoundingBox boundingBox();

        int size();
    }

    private record Leaf<T>(Entry<T> entry, int count) implements Node<T> {

        public Leaf(Entry<T> entry) {
            this(entry, 1);
        }

        @Override
        public BoundingBox boundingBox() {
            return entry.boundingBox();
        }

        @Override
        public int size() {
            return count;
        }

        public @Nullable Leaf<T> withCount(int count) {
            if (count == 0) return null;
            return new Leaf<>(entry, count);
        }
    }

    private record Branch<T>(BoundingBox boundingBox, Node<T> left, Node<T> right) implements Node<T> {

        public Branch(Node<T> left, Node<T> right) {
            this(left.boundingBox().union(right.boundingBox()), left, right);
        }

        public Node<T> withLeft(@Nullable Node<T> left) {
            if (left == null) return right;
            return new Branch<>(left, right);
        }

        public Node<T> withRight(@Nullable Node<T> right) {
            if (right == null) return left;
            return new Branch<>(left, right);
        }

        @Override
        public int size() {
            return left.size() + right.size();
        }
    }

    private Node<T> root;
    private final IntCache size = new IntCache(() -> root == null ? 0 : root.size());

    @Override
    public void insert(T value, BoundingBox boundingBox) {
        size.invalidate();
        root = insertNode(root, new Entry<>(value, boundingBox));
    }

    // Private helper method for inserting nodes in BVH
    private Node<T> insertNode(Node<T> current, Entry<T> entry) {
        if (current == null) {
            return new Leaf<>(entry);
        }

        if (current instanceof Leaf<T> leaf) {
            if (leaf.entry().equals(entry)) {
                return leaf.withCount(leaf.count() + 1);
            }

            // Convert leaf node to branch node
            Node<T> leftLeaf = new Leaf<>(leaf.entry());
            Node<T> rightLeaf = new Leaf<>(entry);
            return new Branch<>(leftLeaf, rightLeaf);
        }

        // Current node is a branch
        Branch<T> branch = (Branch<T>) current;
        if (shouldGoLeft(branch, entry.boundingBox())) {
            return branch.withLeft(insertNode(branch.left, entry));
        } else {
            return branch.withRight(insertNode(branch.right, entry));
        }
    }

    // Determine whether to insert in the left or right subtree (simple heuristic)
    private boolean shouldGoLeft(Branch<T> branch, BoundingBox newBox) {
        double leftArea;
        leftArea = branch.left == null ? Double.MAX_VALUE : branch.left.boundingBox().union(newBox).surfaceArea();
        double rightArea;
        rightArea = branch.right == null ? Double.MAX_VALUE : branch.right.boundingBox().union(newBox).surfaceArea();
        return leftArea < rightArea;
    }

    public void remove(T value, BoundingBox boundingBox) {
        size.invalidate();
        root = removeNode(root, new Entry<>(value, boundingBox));
    }

    // Private helper method for removing nodes from BVH
    private Node<T> removeNode(Node<T> current, Entry<T> entry) {
        if (current == null) return null;

        if (!current.boundingBox().intersects(entry.boundingBox())) {
            return current; // no intersection, no removal
        }

        return switch (current) {
            case Leaf<T> leaf -> leaf.entry().equals(entry) ? leaf.withCount(leaf.count() - 1) : leaf;
            case Branch<T> branch -> {
                // Recursively remove from left and right subtrees
                Node<T> newLeft = removeNode(branch.left, entry);
                Node<T> newRight = removeNode(branch.right, entry);

                // If both subtrees are empty, return null
                if (newLeft == null && newRight == null) {
                    yield null;
                }

                // If one subtree is empty, return the other
                if (newLeft == null) {
                    yield newRight;
                }
                if (newRight == null) {
                    yield newLeft;
                }

                // Otherwise, update the branch node with the new subtrees
                yield new Branch<>(branch.boundingBox(), newLeft, newRight);
            }
        };
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public @NotNull Iterable<Entry<T>> visit(QueryItem queryItem) {
        List<Entry<T>> results = new ArrayList<>();
        queryNode(root, queryItem, results::add);
        return results;
    }

    private void queryNode(Node<T> node, QueryItem queryItem, Consumer<Entry<T>> results) {
        if (node == null) return;

        if (node.boundingBox().intersects(queryItem)) {
            if (node instanceof Leaf<T> leaf) {
                for (int i = 0; i < leaf.count; i++) {
                    results.accept(leaf.entry());
                }
            } else {
                Branch<T> branch = (Branch<T>) node;
                queryNode(branch.left, queryItem, results);
                queryNode(branch.right, queryItem, results);
            }
        }
    }
}
