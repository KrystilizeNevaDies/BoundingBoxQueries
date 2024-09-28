package org.bbq.util;

public class IntCache {

    private final Provider provider;
    private Integer value = null;

    public IntCache(Provider provider) {
        this.provider = provider;
    }

    /** Returns the cached value, or calculates it if it's not cached yet. */
    public int get() {
        if (value == null) {
            value = provider.get();
        }
        return value;
    }

    /** Invalidates the cached value. */
    public void invalidate() {
        value = null;
    }

    public interface Provider {
        int get();
    }
}
