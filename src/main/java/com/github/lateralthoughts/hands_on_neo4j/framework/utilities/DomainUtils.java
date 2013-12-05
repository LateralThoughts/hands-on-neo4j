package com.github.lateralthoughts.hands_on_neo4j.framework.utilities;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.Labeled;
import com.github.lateralthoughts.hands_on_neo4j.framework.annotations.RelationType;

public class DomainUtils {

    /**
     * Will throw either a runtime exception if the given domain instance is null
     * or not annotated as a Node or Relationship abstraction.
     */
    public void checkIsAnnotatedDomain(Class<? extends Domain> domain) throws RuntimeException {
        checkNotNull(domain);
        if (!isProperlyAnnotated(domain)) {
            throw new IllegalArgumentException(format(
                "Class %s should either be annotated with @Labeled or @RelationType",
                domain.getSimpleName()
            ));
        }
    }

    /**
     * Determines whether the current object is considered a Node companion domain class or not,
     * i.e. if @Labeled is present.
     */
    public boolean isNodeClass(Domain domain) {
        return isNodeClass(domain.getClass());
    }

    public boolean isNodeClass(Class<? extends Domain> domain) {
        checkNotNull(domain);
        return domain.isAnnotationPresent(Labeled.class);
    }


    /**
     * Determines whether the current object is considered a Relationship companion domain class
     * or not, i.e. if @RelationType is present.
     */
    public boolean isRelationshipClass(Domain domainInstance) {
        return isRelationshipClass(domainInstance.getClass());
    }

    public boolean isRelationshipClass(Class< ? extends Domain> domainInstance) {
        checkNotNull(domainInstance);
        return domainInstance.isAnnotationPresent(RelationType.class);
    }


    private boolean isProperlyAnnotated(Class<? extends Domain> domainInstance) {
        return isNodeClass(domainInstance) || isRelationshipClass(domainInstance);
    }
}
