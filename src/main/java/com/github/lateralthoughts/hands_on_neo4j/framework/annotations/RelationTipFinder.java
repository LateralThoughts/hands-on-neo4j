package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static com.github.lateralthoughts.hands_on_neo4j.framework.functional.AnnotatedFieldPredicate.ANNOTATED_FIELD;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;

class RelationTipFinder {

    private final Class<? extends Annotation> tipClass;

    private RelationTipFinder(Class<? extends Annotation> tipClass) {
        this.tipClass = tipClass;
    }

    static RelationTipFinder startFinder() {
        return new RelationTipFinder(RelationType.Start.class);
    }

    static RelationTipFinder endFinder() {
        return new RelationTipFinder(RelationType.End.class);
    }

    public Domain find(Domain domain) {
        Field field = findStartField(domain);
        try {
            return (Domain) field.get(domain);
        } catch (IllegalAccessException e) {
            throw propagate(e);
        }
    }

    private Field findStartField(Domain domain) {
        Class<? extends Domain> domainClass = domain.getClass();
        Collection<Field> startFields = from(newArrayList(domainClass.getDeclaredFields()))
            .filter(ANNOTATED_FIELD(tipClass))
            .toList();

        assertThatOnlyOneFieldIsAnnotated(domainClass, startFields);

        return startFields.iterator().next();
    }

    private void assertThatOnlyOneFieldIsAnnotated(Class<? extends Domain> domainClass,
                                                   Collection<Field> startFields) {
        
        if (startFields.size() == 0) {
            throw new IllegalArgumentException(
                format("Relation class %s has no @%s annotated field",
                    domainClass.getSimpleName(),
                    tipClass.getSimpleName()
                )
            );
        }

        if (startFields.size() > 1) {
            throw new IllegalArgumentException(
                format("Relation class %s has too many @%s annotated fields: %s",
                    domainClass.getSimpleName(),
                    tipClass.getSimpleName(),
                    startFields
                )
            );
        }
    }
}
