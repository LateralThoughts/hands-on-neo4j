package com.github.lateralthoughts.hands_on_neo4j.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import org.neo4j.graphdb.RelationshipType;

public class RelationshipTypeAssert extends AbstractAssert<RelationshipTypeAssert, RelationshipType> {
    
    protected RelationshipTypeAssert(RelationshipType actual) {
        super(actual, RelationshipTypeAssert.class);
    }

    public static RelationshipTypeAssert assertThat(RelationshipType relationshipType) {
        return new RelationshipTypeAssert(relationshipType);
    }

    public RelationshipTypeAssert hasName(String name) {
        isNotNull();

        String relationshipName = actual.name();
        if (!relationshipName.equals(name)) {
            failWithMessage("Expected Relationship type to be equal to <%s> but was <%s>", name, relationshipName);
        }

        return this;
    }
}
