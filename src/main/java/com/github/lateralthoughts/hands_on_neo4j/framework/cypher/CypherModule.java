package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.LabelFinder;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.PropertyFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CypherModule {

    @Bean
    public PropertyVisitor providePropertyVisitor(PropertyFinder propertyFinder) {
        return new PropertyVisitor(propertyFinder);
    }

    @Bean
    public NodeVisitor provideNodeVisitor(LabelFinder labelFinder,
                                          PropertyVisitor propertyVisitor) {
        return new NodeVisitor(
            labelFinder,
            propertyVisitor
        );
    }
}
