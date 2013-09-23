package com.github.lateralthoughts.hands_on_neo4j;

import com.github.lateralthoughts.hands_on_neo4j.domain.Branch;
import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.domain.Project;
import com.github.lateralthoughts.hands_on_neo4j.framework.suite.GraphTestSuite;
import com.google.common.base.Function;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Collection;

import static com.github.lateralthoughts.hands_on_neo4j.framework.assertions.Neo4jAssertions.assertThat;
import static com.google.common.collect.Collections2.transform;

public class Exercise_3_Test extends GraphTestSuite {

    @Before
    public void prepare() {
        Project project = new Project("BIRGGIT");

        // create default branch
        Branch branch = new Branch(project, new Commit("0xcafebabe", "Initial commit") );
        Commit secondCommit = new Commit("00124e98ca", "Added some awesomeness");
        Commit thirdCommit = new Commit("123e76cdf", "Fixed so-called 'awesomeness'");
        Commit fourthCommit = new Commit("0xe43770ad", "Release 1.0");
        birggit.init(branch, secondCommit, thirdCommit, fourthCommit);

        // create a new branch - based on default branch's 2nd commit
        Commit prodCommit = new Commit("0xf0f0f0f0", "Release 2.0");
        Branch productionBranch = new Branch(project, secondCommit, "prod");
        birggit.init(productionBranch, prodCommit);
    }

    @Test
    public void _01_should_log_multiple_commits_on_same_branch() {
        Collection<Node> logs = birggit.log(Branch.DEFAULT_BRANCH_NAME);

        try (Transaction tx = graphDB.beginTx()) {

            assertThat(logs).hasSize(4);
            assertThat(extractedCommitHashes(logs)).containsExactly(
                "0xe43770ad", "123e76cdf", "00124e98ca", "0xcafebabe"
            );
            tx.success();
        }
    }

    @Test
    public void _02_should_log_multiple_commits_on_same_branch_with_pagination() {
        Collection<Node> logs = birggit.log(Branch.DEFAULT_BRANCH_NAME, 2);

        try (Transaction tx = graphDB.beginTx()) {

            assertThat(logs).hasSize(2);
            assertThat(extractedCommitHashes(logs)).containsExactly("0xe43770ad", "123e76cdf");
            tx.success();
        }
    }

    @Test
    public void _03_should_log_multiple_commits_on_same_branch_with_pagination_with_complex_history() {
        Collection<Node> commits = birggit.log("prod", 3);

        try (Transaction tx = graphDB.beginTx()) {
            assertThat(commits).hasSize(3);
            assertThat(extractedCommitHashes(commits)).containsExactly(
                "0xf0f0f0f0", "00124e98ca", "0xcafebabe"
            );
            tx.success();
        }
    }

    @Test
    public void _04_should_find_common_ancestor_of_two_branches() {
        Node ancestor = birggit.findCommonAncestor(Branch.DEFAULT_BRANCH_NAME, "prod");

        try (Transaction tx = graphDB.beginTx()) {
            assertThat(ancestor).hasProperty("identifier", "00124e98ca");
            tx.success();
        }
    }

    @Test
    public void _05_should_log_between_two_branches() {
        Collection<Node> commits = birggit.log(Branch.DEFAULT_BRANCH_NAME, "prod");

        try (Transaction tx = graphDB.beginTx()) {
            assertThat(commits).hasSize(2);
            assertThat(extractedCommitHashes(commits)).containsExactly(
                "0xf0f0f0f0", "00124e98ca"
            );
            tx.success();
        }
    }



    private Collection<String> extractedCommitHashes(Collection<Node> logs) {
        return transform(logs, new Function<Node, String>() {
            @Override
            public String apply(Node input) {
                return input.getProperty("identifier").toString();
            }
        });
    }
}
