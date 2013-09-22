package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import static java.lang.String.format;

import javax.inject.Inject;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationTypeFinder;

class RelationVisitor implements CypherVisitor {

    private final RelationTypeFinder relationTypes;
    private final NodeVisitor nodeVisitor;
    private final PropertyVisitor propertyVisitor;


    @Inject
    public RelationVisitor(RelationTypeFinder relationTypes,
                           NodeVisitor nodeVisitor,
                           PropertyVisitor propertyVisitor) {

        this.relationTypes = relationTypes;
        this.nodeVisitor = nodeVisitor;
        this.propertyVisitor = propertyVisitor;
    }

    @Override
    public String visit(Domain domain) {
        return format(
            "%s-[:%s %s]%s%s",
            nodeVisitor.visit(relationTypes.findStart(domain)),
            relationTypes.findRelationshipName(domain),
            propertyVisitor.visit(domain),
            relationTypes.isDirected(domain) ? "->" : "-",
            nodeVisitor.visit(relationTypes.findEnd(domain))
        );
    }
}
