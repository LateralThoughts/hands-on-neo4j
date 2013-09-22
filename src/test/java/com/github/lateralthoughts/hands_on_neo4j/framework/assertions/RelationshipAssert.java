package com.github.lateralthoughts.hands_on_neo4j.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import org.neo4j.graphdb.Relationship;

public class RelationshipAssert extends AbstractAssert<RelationshipAssert, Relationship> {

    protected RelationshipAssert(Relationship actual) {
        super(actual, RelationshipAssert.class);
    }

    public static RelationshipAssert assertThat(Relationship relationship) {
        return new RelationshipAssert(relationship);
    }

    public RelationshipAssert hasType(String name) {
        isNotNull();

        String relationshipName = actual.getType().name();
        if (!relationshipName.equals(name)) {
            failWithMessage("Expected Relationship type to be equal to <%s> but was <%s>", name, relationshipName);
        }

        return this;
    }

    public RelationshipAssert hasProperty(String key, Object value) {
        isNotNull();

        Object foundPropertyValue = actual.getProperty(key, null);
        if (foundPropertyValue == null) {
            failWithMessage("Expected Relationship to have property of key <%s>, but none found.", key);
        }

        if (!foundPropertyValue.equals(value)) {
            failWithMessage(
                "Expected Relationship to have property of key <%s> to be equal to <%s>, but was equal to <%s>.",
                key,
                value,
                foundPropertyValue
            );
        }

        return this;
    }
}
