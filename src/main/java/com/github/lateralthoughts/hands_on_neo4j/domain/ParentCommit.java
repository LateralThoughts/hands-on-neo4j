package com.github.lateralthoughts.hands_on_neo4j.domain;


import static com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType.End;
import static com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType.Start;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Indexed;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Property;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.UniqueIdentifier;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.Cypherizable;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.DomainToCypher;

@Indexed
@RelationType(value = "HAS_PARENT", directed = true)
public class ParentCommit implements Domain, Cypherizable {

    @Start
    private final Commit parent;
    @End
    private final Commit child;

    @UniqueIdentifier("parentCommitIdentifier")
    @Property("identifier")
    private final String identifier;

    public ParentCommit(Commit parent, Commit child) {
        this.parent = parent;
        this.child = child;
        this.identifier = String.format("%s->%s", parent.getIdentifier(), child.getIdentifier());
    }

    public Commit getParent() {
        return parent;
    }

    public Commit getChild() {
        return child;
    }

    @Override
    public String accept(DomainToCypher visitor) {
        checkNotNull(visitor);
        return visitor.cypherize(this);
    }

    @Override
    public String toString() {
        return format("%s-[:HAS_PARENT]->%s", child, parent);
    }
}
