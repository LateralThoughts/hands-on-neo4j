package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;


import static com.github.lateralthoughts.hands_on_neo4j.framework.assertions.Neo4jAssertions.assertThat;

import java.util.Collection;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.FinderModule;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.LabelFinder;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Labeled;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;

import dagger.Module;
import dagger.ObjectGraph;

@Test(groups = "internal")
public class LabelFinderTest {
    
    @Inject
    LabelFinder finder;

    @Inject
    DomainUtils domainUtils;

    @BeforeMethod
    public void prepare() {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void should_throw_exception_when_searching_labels_on_null_class() {
        finder.findAll(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_when_searching_labels_on_not_annotated_class() {
        finder.findAll((new Domain() {}).getClass());
    }

    @Test(dataProvider = "class_names_from_annotation_cases")
    public void should_retrieve_label_from_annotation_attribute(Domain entity, String expectedValue) {
        Collection<org.neo4j.graphdb.Label> labels = finder.findAll(entity.getClass());

        assertThat(labels).hasSize(1);
        assertThat(labels.iterator().next()).hasName(expectedValue);
    }

    @Test(dataProvider = "inferred_from_class_names_cases")
    public void should_retrieve_label_from_class_names(Domain entity, String expectedValue) {
        Collection<org.neo4j.graphdb.Label> labels = finder.findAll(entity.getClass());

        assertThat(labels).hasSize(1);
        assertThat(labels.iterator().next()).hasName(expectedValue);
    }

    @DataProvider
    private Object[][] class_names_from_annotation_cases() {
        @Labeled("bar") class Foo implements Domain {}
        @Labeled("QIX") class Bar implements Domain {}

        return new Object[][] {
            {new Foo(), "bar"},
            {new Bar(), "QIX"}
        };
    }

    @DataProvider
    private Object[][] inferred_from_class_names_cases() {
        @Labeled
        class FooBar implements Domain {}
        @Labeled
        class BarQix implements Domain {}

        return new Object[][] {
            {new FooBar(), "FOO_BAR"},
            {new BarQix(), "BAR_QIX"}
        };
    }

    @Module(
        includes = FinderModule.class,
        library = true,
        injects = LabelFinderTest.class
    )
    static class TestModule {}
}
