package com.github.lateralthoughts.hands_on_neo4j.framework.suite;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GraphSuiteModule {

    @Bean
    public GraphDatabaseService provideGraphDatabaseService() {
        return new TestGraphDatabaseFactory().newImpermanentDatabase();
    }
}
