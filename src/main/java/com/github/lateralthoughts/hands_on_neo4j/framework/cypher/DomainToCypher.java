package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainToCypher {

    private final DomainUtils domainUtils;
    private final NodeVisitor nodeVisitor;
    private final RelationVisitor relationVisitor;

    @Autowired
    public DomainToCypher(DomainUtils domainUtils,
                          NodeVisitor nodeVisitor,
                          RelationVisitor relationVisitor) {
        
        this.domainUtils = domainUtils;
        this.nodeVisitor = nodeVisitor;
        this.relationVisitor = relationVisitor;
    }

    public String cypherize(Domain domain) {

        if (domainUtils.isNodeClass(domain)) {
            return nodeVisitor.visit(domain);
        }
        
        if (domainUtils.isRelationshipClass(domain)) {
            return relationVisitor.visit(domain);
        }
        
        throw new IllegalArgumentException(String.format(
            "Domain class %s is neither a node nor a relationship abstraction",
            domain.getClass().getSimpleName()
        ));
    }

    
}
