package com.github.lateralthoughts.hands_on_neo4j.framework.suite;


import javax.inject.Singleton;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.github.lateralthoughts.hands_on_neo4j.Exercise_1_Test;
import com.github.lateralthoughts.hands_on_neo4j.Exercise_2_Test;
import com.github.lateralthoughts.hands_on_neo4j.Exercise_3_Test;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.FinderModule;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.CypherModule;

import dagger.Module;
import dagger.Provides;

@Module(
    includes = {
        FinderModule.class,
        CypherModule.class
    },
    injects = {
        Exercise_1_Test.class,
        Exercise_2_Test.class,
        Exercise_3_Test.class
    }
)
class GraphSuiteModule {
    @Provides
    @Singleton
    public GraphDatabaseService provideGraphDatabaseService() {
        return new TestGraphDatabaseFactory().newImpermanentDatabase();
    }
}
