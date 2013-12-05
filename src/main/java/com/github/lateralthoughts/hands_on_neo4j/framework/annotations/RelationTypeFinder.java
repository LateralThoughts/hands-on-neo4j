package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.ClassUtils;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationTipFinder.endFinder;
import static com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationTipFinder.startFinder;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Utility class dedicated to relationship type resolution.
 *
 * Its auxiliary goal is to reduce boilerplate and reconcile domain classes with the
 * schema-free nature of Neo4J Relationship entities.
 *
 * @see {link RelationType}
 */
@Component
public class RelationTypeFinder {

    private final ClassUtils classUtils;
    private final DomainUtils domainUtils;
    private final RelationTipFinder startFinder;
    private final RelationTipFinder endFinder;

    @Autowired
    public RelationTypeFinder(ClassUtils classUtils,
                              DomainUtils domainUtils,
                              @Qualifier("start") RelationTipFinder startFinder,
                              @Qualifier("end") RelationTipFinder endFinder) {

        this.classUtils = classUtils;
        this.domainUtils = domainUtils;
        this.startFinder = startFinder;
        this.endFinder = endFinder;
    }

    /**
     * Returns the value of @RelationType as the relationship type of the provided non-null
     * domain class.
     */
    public RelationshipType findRelationshipType(Domain domain) {
        return findRelationshipType(domain.getClass());
    }

    public RelationshipType findRelationshipType(Class<? extends Domain> domain) {
        checkIsDomainRelationshipClass(domain);

        String relationshipType = getRelationshipType(domain);
        if (!relationshipType.isEmpty()) {
            return DynamicRelationshipType.withName(relationshipType);
        }
        return defaultRelationshipType(domain);
    }

    public String findRelationshipName(Domain domain) {
        return findRelationshipType(domain).name();
    }

    public Domain findStart(Domain domain) {
        checkIsDomainRelationshipClass(domain.getClass());
        return startFinder().find(domain);
    }

    public Domain findEnd(Domain domain) {
        checkIsDomainRelationshipClass(domain.getClass());
        return endFinder().find(domain);
    }

    public boolean isDirected(Domain domain) {
        return isDirected(domain.getClass());
    }

    public boolean isDirected(Class<? extends Domain> domain) {
        checkIsDomainRelationshipClass(domain);
        RelationType annotation = domain.getAnnotation(RelationType.class);
        return annotation.directed();
    }

    private void checkIsDomainRelationshipClass(Class<? extends Domain> domainInstance) {
        checkNotNull(domainInstance);
        if (!domainUtils.isRelationshipClass(domainInstance)) {
            throw new IllegalArgumentException(format(
                "Domain class %s should be annotated with @RelationType",
                domainInstance.getClass().getSimpleName()
            ));
        }
    }

    private String getRelationshipType(Class<? extends Domain> domain) {
        return domain.getAnnotation(RelationType.class).value();
    }

    private RelationshipType defaultRelationshipType(Class<? extends Domain> domainClass) {
        return DynamicRelationshipType.withName(classUtils.toUpperUnderscoreNotation(domainClass));
    }
}
