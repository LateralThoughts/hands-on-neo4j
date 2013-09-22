package com.github.lateralthoughts.hands_on_neo4j.framework.suite;

import javax.inject.Inject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.lateralthoughts.hands_on_neo4j.frontend.BIRGGIT;
import dagger.ObjectGraph;

@Test(groups = "hands_on")
public abstract class GraphTestSuite {

    @Inject
    protected GraphDatabaseService graphDB;

    @Inject
    protected BIRGGIT birggit;

    @BeforeMethod
    public void prepare() {
        ObjectGraph.create(GraphSuiteModule.class).inject(this);
    }

    @AfterMethod
    public void cleanUp() {
        graphDB.shutdown();
    }

}
