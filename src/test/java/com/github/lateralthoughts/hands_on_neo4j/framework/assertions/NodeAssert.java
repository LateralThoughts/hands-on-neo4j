package com.github.lateralthoughts.hands_on_neo4j.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import org.neo4j.graphdb.Node;

public class NodeAssert extends AbstractAssert<NodeAssert, Node> {

    protected NodeAssert(Node actual) {
        super(actual, NodeAssert.class);
    }

    public static NodeAssert assertThat(Node actual) {
        return new NodeAssert(actual);
    }

    public NodeAssert hasProperty(String key, Object value) {
        isNotNull();

        Object foundPropertyValue = actual.getProperty(key, null);
        if (foundPropertyValue == null) {
            failWithMessage("Expected Node to have property of key <%s>, but none found.", key);
        }

        if (!foundPropertyValue.equals(value)) {
            failWithMessage(
                "Expected Node property (key <%s>) to be equal to <%s>, but was equal to <%s>.",
                key,
                value,
                foundPropertyValue
            );
        }

        return this;
    }
}
