package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationTypeFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
class RelationVisitor implements CypherVisitor {

    private final RelationTypeFinder relationTypes;
    private final NodeVisitor nodeVisitor;
    private final PropertyVisitor propertyVisitor;


    @Autowired
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
