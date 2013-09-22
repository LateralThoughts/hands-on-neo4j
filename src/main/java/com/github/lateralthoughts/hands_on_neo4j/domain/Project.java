package com.github.lateralthoughts.hands_on_neo4j.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import java.util.Objects;

import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Indexed;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Labeled;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Property;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.UniqueIdentifier;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.Cypherizable;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.DomainToCypher;

@Indexed
@Labeled
public class Project implements Domain, Cypherizable {

    @UniqueIdentifier("projectName")
    @Property("name")
    private final String name;

    public Project(String name) {
        this.name = name;
        checkState(this.name != null && !this.name.isEmpty());
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
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        return equal(name, ((Project) other).name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return format("(:PROJECT {name: %s})", name);
    }
}
