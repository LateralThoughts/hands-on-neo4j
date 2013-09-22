package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;

import dagger.Module;
import dagger.ObjectGraph;

@Test(groups = "internal")
public class UniqueIdentifierFinderTest {

    @Inject
    UniqueIdentifierFinder finder;

    @Inject
    DomainUtils domainUtils;

    @BeforeMethod
    public void prepare() {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void should_throw_exception_if_instance_is_null() {
        finder.findSingleKeyValue(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_if_instance_is_not_an_annotated_domain_class() {
        finder.findSingleKeyValue(new Domain() {
        });
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_if_no_identity_annotation_found_on_domain_instance() {
        @Labeled
        class MyHammer implements Domain {}
        finder.findSingleKeyValue(new MyHammer());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_if_more_than_one_annotation_found_on_domain_instance() {
        @RelationType
        class MyHammer implements Domain {
            @UniqueIdentifier("foo")
            @SuppressWarnings("unused")
            String foo;
            @UniqueIdentifier("bar")
            @SuppressWarnings("unused")
            String bar;
        }

        finder.findSingleKeyValue(new MyHammer());
    }

    @Test
    public void should_find_explicit_identity_property() {
        @RelationType
        class HammerTime implements Domain {
            @UniqueIdentifier("uuid")
            @SuppressWarnings("unused")
            private String login = "admin";

        }

        Entry<String, Object> uniqueProperty = finder.findSingleKeyValue(new HammerTime());
        assertThat(uniqueProperty.getKey()).isEqualTo("uuid");
        assertThat(uniqueProperty.getValue()).isEqualTo("admin");
    }

    @Module(
        includes = FinderModule.class,
        library = true,
        injects = UniqueIdentifierFinderTest.class
    )
    static class TestModule {}
}
