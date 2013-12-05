package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.LabelFinder;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
class NodeVisitor implements CypherVisitor {

    private final LabelFinder labels;
    private final PropertyVisitor propertyVisitor;

    @Autowired
    public NodeVisitor(LabelFinder labels,
                       PropertyVisitor propertyVisitor) {

        this.labels = labels;
        this.propertyVisitor = propertyVisitor;
    }

    @Override
    public String visit(Domain domain) {
        return format("(n%s %s)",
            visitLabels(domain),
            propertyVisitor.visit(domain)
        );
    }

    private String visitLabels(Domain domain) {
        return new StringBuilder(":")
            .append(Joiner.on(":").join(labels.findAllNames(domain.getClass())))
            .toString();
    }
}
