package com.github.lateralthoughts.hands_on_neo4j.framework.functional;

import static com.google.common.base.Preconditions.checkNotNull;

import org.neo4j.graphdb.Relationship;

import com.google.common.base.Predicate;

public final class RelationshipHasTypePredicate implements Predicate<Relationship> {

    private final String label;

    private RelationshipHasTypePredicate(String label) {
        this.label = checkNotNull(label);
    }

    public static RelationshipHasTypePredicate HAS_TYPE(String label) {
        return new RelationshipHasTypePredicate(label);
    }

    @Override
    public boolean apply(Relationship input) {
        return label.equals(input.getType().name());
    }
}
