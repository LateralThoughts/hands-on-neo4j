package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static com.github.lateralthoughts.hands_on_neo4j.framework.assertions.Neo4jAssertions.assertThat;

import javax.inject.Inject;

import org.neo4j.graphdb.RelationshipType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;

import dagger.Module;
import dagger.ObjectGraph;

@Test(groups = "internal")
public class RelationTypeFinderTest {

    @Inject
    RelationTypeFinder finder;

    @BeforeMethod
    public void prepare() {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void should_throw_exception_when_searching_relationship_type_on_null_class() {
        finder.findRelationshipType((Domain) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_when_searching_relationship_type_on_not_annotated_class() {
        finder.findRelationshipType(new Domain() {});
    }

    @Test
    public void should_find_explicit_relationship_type() {
        @RelationType("LOVING")
        class It implements Domain {}

        RelationshipType relationshipType = finder.findRelationshipType(new It());
        assertThat(relationshipType.name()).isEqualTo("LOVING");
    }

    @Test
    public void should_find_implicit_relationship_type() {
        @RelationType
        class ILoveNeo4j implements Domain {}

        RelationshipType relationshipType = finder.findRelationshipType(new ILoveNeo4j());
        assertThat(relationshipType.name()).isEqualTo("I_LOVE_NEO4J");
    }

    @Module(
        includes = FinderModule.class,
        library = true,
        injects = RelationTypeFinderTest.class
    )
    static class TestModule {}
}
