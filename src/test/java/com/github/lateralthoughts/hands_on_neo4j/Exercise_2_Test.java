package com.github.lateralthoughts.hands_on_neo4j;

import com.github.lateralthoughts.hands_on_neo4j.domain.Branch;
import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.domain.ParentCommit;
import com.github.lateralthoughts.hands_on_neo4j.domain.Project;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationTypeFinder;
import com.github.lateralthoughts.hands_on_neo4j.framework.suite.GraphTestSuite;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.lateralthoughts.hands_on_neo4j.framework.assertions.Neo4jAssertions.assertThat;

public class Exercise_2_Test extends GraphTestSuite {

    private Branch mainBranch;

    private Branch featureBranch;

    @Autowired
    RelationTypeFinder relationTypes;

    @Before
    public void prepare() {

        mainBranch = new Branch(
            new Project("BIRGGIT"),
            new Commit("master_HEAD", "Fixed everything, and beyond!")
        );

        featureBranch = new Branch(
            new Project("BIRGGIT"),
            new Commit("feature_HEAD", "Added the definitive feature!"),
            "feature"
        );

        try (Transaction tx = graphDB.beginTx()) {
            birggit.indexBranch(birggit.createBranch(mainBranch));
            birggit.indexBranch(birggit.createBranch(featureBranch));
            tx.success();
        }
    }

    @Test
    public void _01_should_find_latest_commit_on_main_branch() {
        Relationship mainBranch = birggit.findBranch(Branch.DEFAULT_BRANCH_NAME);

        try (Transaction tx = graphDB.beginTx()) {
            Node headCommit = mainBranch.getEndNode();
            assertThat(headCommit).hasProperty("identifier", "master_HEAD");
            tx.success();
        }
    }

    @Test
    public void _02_should_commit_on_main_branch() {
        Relationship branch = birggit.commit(
            new Commit("new_master_HEAD", "Fix last fix"),
            mainBranch
        );

        try (Transaction tx = graphDB.beginTx()) {
            Node headCommit = branch.getEndNode();
            
            assertThat(headCommit).hasProperty("identifier", "new_master_HEAD");
            assertThat(
                headCommit.getSingleRelationship(
                    relationTypes.findRelationshipType(ParentCommit.class),
                    Direction.INCOMING
                ).getStartNode()
            ).hasProperty("identifier", "master_HEAD");
            tx.success();
        }
    }


    @Test
    public void _03_should_merge_feature_and_master_branches() {
        Relationship newFeatureBranch = birggit.merge(featureBranch, mainBranch);

        try (Transaction tx = graphDB.beginTx()) {
            Node newHeadCommit = newFeatureBranch.getEndNode();

            assertThat(newHeadCommit).hasProperty(
                "message",
                "Merged " + Branch.DEFAULT_BRANCH_NAME
            );

            assertThat(newHeadCommit.getRelationships(
                relationTypes.findRelationshipType(ParentCommit.class),
                Direction.INCOMING
            ).iterator()).hasSize(2);
            
            tx.success();
        }
    }
}
