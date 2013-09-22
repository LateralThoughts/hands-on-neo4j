package com.github.lateralthoughts.hands_on_neo4j.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.UniqueIdentifier;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Indexed;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Labeled;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Property;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.Cypherizable;
import com.github.lateralthoughts.hands_on_neo4j.framework.cypher.DomainToCypher;
import com.google.common.base.Objects;

@Indexed
@Labeled("COMMIT")
public class Commit implements Domain, Cypherizable {
    @UniqueIdentifier("commitIdentifier")
    @Property("identifier")
    private final String identifier;
    @Property("message")
    private final String message;

    public Commit(String identifier, String message) {
        this.identifier = identifier;
        this.message = message;
        checkState(identifier != null);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMessage() {
        return message;
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

        return Objects.equal(identifier, ((Commit) other).identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return format("(:COMMIT {identifier: %s, message: %s})", identifier, message);
    }
}
