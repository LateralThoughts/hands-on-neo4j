package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.lateralthoughts.hands_on_neo4j.domain.Branch;
import com.github.lateralthoughts.hands_on_neo4j.domain.Commit;
import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.domain.Project;

import dagger.Module;
import dagger.ObjectGraph;

@Test(groups = "internal")
public class DomainToCypherTest {

    @Inject
    DomainToCypher domainToCypher;

    @BeforeMethod
    public void prepare() {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void should_throw_an_exception_if_no_node_or_relationship_abstraction_is_passed() {
        domainToCypher.cypherize(new Domain() {});
    }

    @Test
    public void should_visit_commit() {
        Commit commit = new Commit("0xcafebabe", "Hello world");
        String statement = domainToCypher.cypherize(commit);

        assertThat(statement)
            .isEqualTo(
                "(n:COMMIT {identifier:'0xcafebabe',message:'Hello world'})"
            );
    }

    @Test
    public void should_visit_project() {
        Project project = new Project("BIRGGIT");
        String statement = domainToCypher.cypherize(project);

        assertThat(statement)
            .isEqualTo(
                "(n:PROJECT {name:'BIRGGIT'})"
            );
    }

    @Test
    public void should_visit_branch() {
        Branch branch = new Branch(
            new Project("BIRGGIT"),
            new Commit("0xbwahahaha", "Breaks!")
        );

        String statement = domainToCypher.cypherize(branch);

        assertThat(statement)
            .isEqualTo(
                "(n:PROJECT {name:'BIRGGIT'})" +
                    "-[:HAS_BRANCH {name:'munster'}]->" +
                    "(n:COMMIT {identifier:'0xbwahahaha',message:'Breaks!'})"
            );
    }

    @Module(
        includes = CypherModule.class,
        library = true,
        injects = DomainToCypherTest.class
    )
    static class TestModule {}
}
