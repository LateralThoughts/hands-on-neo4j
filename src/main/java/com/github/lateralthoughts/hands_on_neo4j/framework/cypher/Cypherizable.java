package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

public interface Cypherizable {

    String accept(DomainToCypher visitor);
}
