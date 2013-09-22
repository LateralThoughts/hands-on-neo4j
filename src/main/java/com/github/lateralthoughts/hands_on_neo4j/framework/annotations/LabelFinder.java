package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.*;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.neo4j.graphdb.DynamicLabel.label;

import java.util.Collection;

import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;
import org.neo4j.graphdb.Label;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.ClassUtils;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * Utility class responsible for Neo4J label resolution.
 *
 * Its auxiliary goal is to reduce boilerplate and reconcile domain classes with the
 * schema-free nature of Neo4J Node entities.
 *
 * @see {link Labeled}
 */
public class LabelFinder {

    private final ClassUtils classUtils;
    private final DomainUtils domainUtils;

    @Inject
    public LabelFinder(ClassUtils classUtils,
                       DomainUtils domainUtils) {
        this.classUtils = classUtils;
        this.domainUtils = checkNotNull(domainUtils);
    }

    public Collection<String> findAllNames(Class<? extends Domain> entity) {
        return FluentIterable.from(findAll(entity))
            .transform(new Function<Label, String>() {
                @Override
                public String apply(org.neo4j.graphdb.Label input) {
                    if (input == null) {
                        return null;
                    }
                    return input.name();
                }
            })
            .filter(notNull())
            .toList();
    }

    /**
     * Determines the node labels from the provided non-null domain class.
     * If no value is provided, the label is inferred from the class name.
     *
     * @throws NullPointerException if entity is null
     * @throws IllegalArgumentException if entity is not properly annotated
     * @return an *immutable* collection representation of the configured labels
     */
    public Label[] findAllLabels(Class<? extends Domain> entity) {
        return findAll(entity).toArray(new Label[0]);
    }

    @VisibleForTesting
    Collection<org.neo4j.graphdb.Label> findAll(Class<? extends Domain> entity) {
        checkIsDomainNode(entity);

        String[] values = entity.getAnnotation(Labeled.class).value();
        if (values.length > 0) {
            return parseLabels(values);
        }
        return defaultLabel(entity);
    }

    private void checkIsDomainNode(Class<? extends Domain> domainInstance) {
        if (!domainUtils.isNodeClass(domainInstance)) {
            throw new IllegalArgumentException(format(
                "Domain class %s should be annotated with @Labeled",
                domainInstance.getClass().getSimpleName()
            ));
        }
    }

    private void checkSingleLabel(Domain entity, Collection<org.neo4j.graphdb.Label> labels) {
        int size = labels.size();
        checkArgument(
            size == 1,
            format("%d label values found on class %s. Expected 1.", size, entity.getClass().getSimpleName())
        );
    }

    private Collection<org.neo4j.graphdb.Label> parseLabels(String[] values) {
        return FluentIterable.from(newArrayList(values))
            .filter(not(equalTo("")))
            .transform(
                new Function<String, org.neo4j.graphdb.Label>() {
                    @Override
                    public org.neo4j.graphdb.Label apply(String input) {
                        return label(input);
                    }
                }
            )
            .toSet();
    }

    private Collection<org.neo4j.graphdb.Label> defaultLabel(Class<? extends Domain> entityClass) {
        return ImmutableList.of(label(classUtils.toUpperUnderscoreNotation(entityClass)));
    }
}
