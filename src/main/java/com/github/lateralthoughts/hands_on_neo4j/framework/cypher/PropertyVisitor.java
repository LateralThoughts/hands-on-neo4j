package com.github.lateralthoughts.hands_on_neo4j.framework.cypher;

import static com.google.common.base.Predicates.notNull;
import static java.lang.String.format;

import java.util.Collection;

import javax.inject.Inject;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.PropertyFinder;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

class PropertyVisitor implements CypherVisitor {

    private final PropertyFinder properties;

    @Inject
    public PropertyVisitor(PropertyFinder properties) {
        this.properties = properties;
    }
    
    @Override
    public String visit(Domain domain) {
        String properties = Joiner.on(",").join(entriesToStrings(domain));
        return new StringBuilder("{")
            .append(properties)
            .append("}")
            .toString();
    }

    private Collection<String> entriesToStrings(Domain domain) {
        return FluentIterable.from(properties.findAll(domain))
            .transform(recursivelyAsStrings())
            .filter(notNull())
            .toList();
    }

    private Function<Entry<String, Object>, String> recursivelyAsStrings() {
        return new Function<Entry<String, Object>, String>() {
            @Override
            public String apply(Entry<String, Object> input) {
                if (input == null) {
                    return null;
                }
                return format("%s:'%s'", input.getKey(), transformValue(input));
            }

            private String transformValue(Entry<String, Object> input) {
                Object value = input.getValue();
                if (!(value instanceof Domain)) {
                    return value.toString();
                }
                return PropertyVisitor.this.visit((Domain) value);
            }
        };
    }
}
