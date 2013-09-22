package com.github.lateralthoughts.hands_on_neo4j.framework.assertions;

import static com.google.common.collect.Iterators.size;
import static org.assertj.core.util.Lists.newArrayList;

import java.util.Collection;

import org.assertj.core.api.AbstractAssert;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;

public class ResourceIterableAssert extends AbstractAssert<ResourceIterableAssert, ResourceIterable<?>> {

    protected ResourceIterableAssert(ResourceIterable<?> actual) {
        super(actual, ResourceIterableAssert.class);
    }

    public static ResourceIterableAssert assertThat(ResourceIterable iterable) {
        return new ResourceIterableAssert(iterable);
    }

    public ResourceIterableAssert hasSize(int expectedSize) {
        isNotNull();

        int actualSize = size(actual.iterator());
        if (actualSize != expectedSize) {
            failWithMessage("Expected ResourceIterable size to be <%s> but was <%s>", expectedSize, actualSize);
        }

        return this;
    }

    public ResourceIterableAssert contains(Object... objects) {
        isNotNull();

        Collection<Object> values = newArrayList(objects);

        boolean matches = true;
        StringBuilder builder = new StringBuilder();
        ResourceIterator<?> iterator = actual.iterator();

        while (matches && iterator.hasNext()) {
            Object value = iterator.next();
            matches = values.contains(value);
            if (!matches) {
                builder.append(String.format("Value <%s> not found", value));
            }
        }

        if (!matches) {
            failWithMessage(builder.toString());
        }

        return this;
    }
}
