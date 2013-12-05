package com.github.lateralthoughts.hands_on_neo4j;

import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.framework.suite.GraphTestSuite;
import com.google.common.base.Function;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Collection;

import static com.github.lateralthoughts.hands_on_neo4j.framework.assertions.Neo4jAssertions.assertThat;
import static com.google.common.collect.Collections2.transform;


public class Exercise_4_Test extends GraphTestSuite {

    @Before
    public void prepare() {
        try (Transaction tx = graphDB.beginTx()) {
            Node commit = birggit.commit(new Commit("super_id", "Awesomely awesome"));
            birggit.amend(commit, "Oops, forgot that!");
            
            tx.success();
        }
    }

    @Test
    public void _01_should_find_commit_by_hash() {
        try (Transaction tx = graphDB.beginTx()) {
            Node commit = birggit.findOneCommit("super_id");
    
            assertThat(commit).hasProperty("message", "Awesomely awesome");
            tx.success();
        }
    }

    @Test
    public void _02_should_delete_commit_by_identifier() {
        try (Transaction tx = graphDB.beginTx()) {
            birggit.delete("super_id");

            Node commit = birggit.findOneCommit("super_id");
            assertThat(commit).isNull();
            tx.success();
        }
    }

    @Test
    public void _03_should_delete_commit_by_identifiers() {
        try (Transaction tx = graphDB.beginTx()) {
            birggit.commit(new Commit("another_id", "And another fix..."));
            birggit.deleteAll("super_id", "another_id");

            assertThat(birggit.findOneCommit("super_id")).isNull();
            assertThat(birggit.findOneCommit("another_id")).isNull();
            tx.success();
        }
    }

    @Test
    public void _04_should_find_disconnected_commits() {
        try (Transaction tx = graphDB.beginTx()) {
            Collection<Node> orphanedCommits = birggit.findOrphanCommits();
    
            assertThat(extractedCommitMessages(orphanedCommits))
                .hasSize(2)
                .containsOnly("Awesomely awesome", "Oops, forgot that!");
            tx.success();
        }
    }

    @Test
    public void _05_should_collect_garbage_commits() {
        try (Transaction tx = graphDB.beginTx()) {
            birggit.gc();
    
            assertThat(birggit.findOrphanCommits()).isEmpty();
            tx.success();
        }
    }



    private Collection<String> extractedCommitMessages(Collection<Node> logs) {
        return transform(logs, new Function<Node, String>() {
            @Override
            public String apply(Node input) {
                return input.getProperty("message").toString();
            }
        });
    }
}
