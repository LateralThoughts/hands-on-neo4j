package com.github.lateralthoughts.hands_on_neo4j.domain;

import static com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType.End;
import static com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType.Start;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.UniqueIdentifier;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Indexed;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Property;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.Cypherizable;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.DomainToCypher;
import com.google.common.base.Objects;

@Indexed
@RelationType(value = "HAS_BRANCH", directed = true)
public class Branch implements Domain, Cypherizable {

    public static final String DEFAULT_BRANCH_NAME = "munster";

    @Start
    private final Project project;
    @End
    private final Commit commit;

    @UniqueIdentifier("branchName")
    @Property("name")
    private final String name;

    public Branch(Project project, Commit commit, String name) {
        this.project = project;
        this.commit = commit;
        this.name = name;
    }

    public Branch(Project project, Commit commit) {
        this(project, commit, DEFAULT_BRANCH_NAME);
    }

    public Project getProject() {
        return project;
    }

    public Commit getCommit() {
        return commit;
    }

    public String getName() {
        return name;
    }

    @Override
    public String accept(DomainToCypher visitor) {
        checkNotNull(visitor);
        return visitor.cypherize(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Branch other = (Branch) o;
        return equal(project, other.project) &&
            equal(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return format("%s-[:HAS_BRANCH {name:%s}]->%s", project, name, commit);
    }
}
