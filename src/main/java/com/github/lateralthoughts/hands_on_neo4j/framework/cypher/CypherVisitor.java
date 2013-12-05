package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;

interface CypherVisitor {

    public String visit(Domain domain);
}
