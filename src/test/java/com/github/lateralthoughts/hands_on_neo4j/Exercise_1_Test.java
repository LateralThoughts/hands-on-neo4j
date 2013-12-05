package com.github.lateralthoughts.hands_on_neo4j;

import com.github.lateralthoughts.hands_on_neo4j.domain.Branch;
import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.domain.Project;
import com.github.lateralthoughts.hands_on_neo4j.framework.suite.GraphTestSuite;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.tooling.GlobalGraphOperations;
import org.springframework.test.context.ContextConfiguration;

import static com.github.lateralthoughts.hands_on_neo4j.framework.assertions.Neo4jAssertions.assertThat;
import static org.neo4j.graphdb.DynamicLabel.label;


public class Exercise_1_Test extends GraphTestSuite {

    @Test
    public void _01_should_create_one_project() {

        birggit.createNewProject(new Project("BIRGGIT"));

        try (Transaction transaction = graphDB.beginTx()) {
            ResourceIterable<Node> projects = graphDB.findNodesByLabelAndProperty(
                label("PROJECT"),
                "name",
                "BIRGGIT"
            );

            assertThat(projects).hasSize(1);

            Node actual = projects.iterator().next();
            ResourceIterable<Label> labels = actual.getLabels();

            assertThat(labels).hasSize(1) .contains(label("PROJECT"));
            assertThat(actual).hasProperty("name", "BIRGGIT");

            transaction.success();
        }
    }

    @Test
    public void _02_should_create_one_commit() {
        birggit.commit(new Commit("SHAWAN", "Hi, it's my first commit!!"));

        try (Transaction transaction = graphDB.beginTx()) {
            ResourceIterable<Node> commits = graphDB.findNodesByLabelAndProperty(
                label("COMMIT"),
                "identifier",
                "SHAWAN"
            );

            assertThat(commits).hasSize(1);

            Node commit = commits.iterator().next();

            assertThat(commit.getLabels())
                .hasSize(1)
                .contains(label("COMMIT"));

            assertThat(commit)
                .hasProperty("identifier", "SHAWAN")
                .hasProperty("message", "Hi, it's my first commit!!");

            transaction.success();
        }
    }


    @Test
    public void _03_should_amend_an_existing_commit() {
        Node commit = birggit.commit(new Commit("0xcafebabe", "Release SNAPSHIT"));
        birggit.amend(commit, "Oops, release SNAPSHOT");

        try (Transaction transaction = graphDB.beginTx()) {
            ResourceIterable<Node> nodes = graphDB.findNodesByLabelAndProperty(
                label("COMMIT"),
                "identifier",
                "0xcafebabe_amended"
            );

            assertThat(nodes).hasSize(1);

            assertThat(nodes.iterator().next())
                .hasProperty("message", "Oops, release SNAPSHOT");

            transaction.success();
        }
    }

    @Test
    public void _04_should_create_a_branch_on_a_project() {
        Branch branch = new Branch(
            new Project("K2000"),
            new Commit("DavidHash-ellof", "Hello Michael")
        );

        Relationship branchRelation = birggit.createBranch(branch);

        try (Transaction transaction = graphDB.beginTx()) {
            Node queriedNode = graphDB.getNodeById(branchRelation.getStartNode().getId());

            assertThat(queriedNode.getRelationships()).hasSize(1);

            Relationship relationship = queriedNode.getRelationships().iterator().next();
            assertThat(relationship).hasType("HAS_BRANCH");
            assertThat(relationship).hasProperty("name", "munster");

            transaction.success();
        }
    }

    @Test
    public void bonus_should_not_create_multiple_entities() {

        birggit.createBranch(new Branch(
            new Project("GIT"),
            new Commit("1", "Fuck SVN")
        ));

        birggit.createBranch(new Branch(
            new Project("GIT"),
            new Commit("2", "DVCS FTW"),
            "production"
        ));

        try (Transaction transaction = graphDB.beginTx()) {
            GlobalGraphOperations globalOps = GlobalGraphOperations.at(graphDB);
            
            // this refers to the same project name
            assertThat(globalOps.getAllNodesWithLabel(label("PROJECT"))).hasSize(1);
            assertThat(globalOps.getAllNodesWithLabel(label("COMMIT"))).hasSize(2);
            assertThat(globalOps.getAllRelationships()).hasSize(2);
            
            Iterable<RelationshipType> relationshipTypes = globalOps.getAllRelationshipTypes();
            assertThat(relationshipTypes).hasSize(1);
            assertThat(relationshipTypes.iterator().next()).hasName("HAS_BRANCH");

            transaction.success();
        }
    }
}
