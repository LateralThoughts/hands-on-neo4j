package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.FinderModule;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.LabelFinder;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.PropertyFinder;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.UtilsModule;

import dagger.Module;
import dagger.Provides;

@Module(library = true, includes = {
    FinderModule.class,
    UtilsModule.class,
})
public class CypherModule {

    @Provides
    public PropertyVisitor providePropertyVisitor(PropertyFinder propertyFinder) {
        return new PropertyVisitor(propertyFinder);
    }

    @Provides
    public NodeVisitor provideNodeVisitor(LabelFinder labelFinder,
                                          PropertyVisitor propertyVisitor) {
        return new NodeVisitor(
            labelFinder,
            propertyVisitor
        );
    }
}
