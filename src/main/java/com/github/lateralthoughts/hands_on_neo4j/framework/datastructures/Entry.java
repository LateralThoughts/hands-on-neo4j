package com.github.lateralthoughts.hands_on_neo4j.framework.datastructures;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * The poor dev Tuple class...
 */
public final class Entry<K extends String, V> {

    private final K key;
    private final V value;

    public Entry(K key, V value) {

        this.key = key;
        this.value = value;
    }

    public static <K extends String, V>  Function<Entry<K, V>, K> asKeys() {
        return new Function<Entry<K, V>, K>() {
            @Override
            public K apply(Entry<K, V> input) {
                if (input == null) {
                    return null;
                }
                return input.getKey();
            }
        };
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Objects.equal(key, ((Entry) o).getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
