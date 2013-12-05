package com.github.lateralthoughts.hands_on_neo4j.framework.suite;

import com.github.lateralthoughts.hands_on_neo4j.frontend.BIRGGIT;
import org.junit.After;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:applicationContext-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class GraphTestSuite {

    @Autowired
    protected GraphDatabaseService graphDB;

    @Autowired
    protected BIRGGIT birggit;

    @After
    public void cleanUp() {
        //graphDB.shutdown();
    }

}
