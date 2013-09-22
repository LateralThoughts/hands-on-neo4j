package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.Collection;

import javax.inject.Inject;

import org.assertj.core.data.MapEntry;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.google.common.base.Function;

import dagger.Module;
import dagger.ObjectGraph;

@Test(groups = "internal")
public class PropertyFinderTest {

    @Inject
    PropertyFinder propertyFinder;

    @BeforeMethod
    public void prepare() {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void should_throw_exception_because_entity_is_null() {
        propertyFinder.findAll(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_because_class_is_not_annotated() {
        propertyFinder.findAll(new Domain() {});
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_exception_because_several_properties_share_the_same_name() {
        @Labeled
        class Failing implements Domain {
            @Property
            @SuppressWarnings("unused")
            private String foo;
            @Property("foo")
            @SuppressWarnings("unused")
            private String bar;
        }
        propertyFinder.findAll(new Failing());
    }

    @Test(dataProvider = "explicit_properties")
    public void should_return_annotated_explicit_properties(Domain entity, MapEntry[] expectedProperties) {
        Collection<Entry<String,Object>> properties = propertyFinder.findAll(entity);

        Collection<MapEntry> mapEntries = newArrayList(expectedProperties);
        assertThat(properties).hasSize(mapEntries.size());
        assertThat(properties).containsAll(transform(mapEntries, asEntries()));
    }

    @Test(dataProvider = "implicit_properties")
    public void should_return_annotated_implicit_properties(Domain entity, MapEntry[] expectedProperties) {
        Collection<Entry<String, Object>> properties = propertyFinder.findAll(entity);

        Collection<MapEntry> mapEntries = newArrayList(expectedProperties);
        assertThat(properties).hasSize(mapEntries.size());
        assertThat(properties).containsAll(transform(mapEntries, asEntries()));
    }

    @DataProvider
    private Object[][] explicit_properties() {
        @Labeled
        class Foo implements Domain {
            @SuppressWarnings("unused")
            private String f;
        }
        @Labeled
        class Bar implements Domain {
            @Property("oh")
            @SuppressWarnings("unused")
            private String u = "he";
        }
        @Labeled
        class Qix implements Domain {
            @Property("ohoh")
            @SuppressWarnings("unused")
            private String c = "hehe";
            @Property("ohohoh")
            @SuppressWarnings("unused")
            private String k = "hehehe";
        }
        return new Object[][] {
            {new Foo(), new MapEntry[0]},
            {new Bar(), new MapEntry[] {entry("oh", "he")}},
            {new Qix(), new MapEntry[] {entry("ohoh", "hehe"), entry("ohohoh", "hehehe")}}
        };
    }

    @DataProvider
    private Object[][] implicit_properties() {
        @Labeled
        class Hello implements Domain {
            @Property
            @SuppressWarnings("unused")
            private String w = "he";
        }
        @Labeled
        class World implements Domain {
            @Property
            @SuppressWarnings("unused")
            private String t = "hehe";
            @Property
            @SuppressWarnings("unused")
            private String f = "hehehe";
        }
        return new Object[][] {
            {new Hello(), new MapEntry[] {entry("w", "he")}},
            {new World(), new MapEntry[] {entry("t", "hehe"),
                                          entry("f", "hehehe")}}
        };
    }

    private Function<MapEntry, Entry<String , Object>> asEntries() {
        return new Function<MapEntry, Entry<String, Object>>() {
            @Override
            public Entry<String, Object> apply(final MapEntry input) {
                if (input == null) {
                    return null;
                }
                return new Entry<>(input.key.toString(), input.value);
            }
        };
    }

    @Module(
        includes = FinderModule.class,
        library = true,
        injects = PropertyFinderTest.class
    )
    static class TestModule {}
}
