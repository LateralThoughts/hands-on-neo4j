package com.github.lateralthoughts.hands_on_neo4j.framework.assertions;

import org.assertj.core.api.Assertions;
import org.neo4j.graphdb.*;

public class Neo4jAssertions extends Assertions {

    public static ResourceIterableAssert assertThat(ResourceIterable<?> actual) {
        return new ResourceIterableAssert(actual);
    }

    public static LabelAssert assertThat(Label actual) {
        return new LabelAssert(actual);
    }

    public static NodeAssert assertThat(Node actual) {
        return new NodeAssert(actual);
    }

    public static RelationshipAssert assertThat(Relationship actual) {
        return new RelationshipAssert(actual);
    }

    public static RelationshipTypeAssert assertThat(RelationshipType actual) {
        return new RelationshipTypeAssert(actual);
    }
}
