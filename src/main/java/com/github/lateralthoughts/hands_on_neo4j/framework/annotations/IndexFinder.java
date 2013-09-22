package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.ClassUtils;

/**
 * Utility class responsible for Neo4J index name resolution.
 */
public class IndexFinder {

    private final ClassUtils classUtils;

    @Inject
    public IndexFinder(ClassUtils classUtils) {
        this.classUtils = classUtils;
    }
    
    public String findName(Domain domainInstance) {
        checkIsAnnotated(domainInstance);
        return findName(domainInstance.getClass());
    }

    public String findName(Class<? extends Domain> domainClass) {
        String indexName = domainClass.getAnnotation(Indexed.class).value();
        if (!indexName.isEmpty()) {
            return indexName;
        }
        return classUtils.toUpperUnderscoreNotation(domainClass);
    }

    private static void checkIsAnnotated(Domain domainClass) {
        checkNotNull(domainClass);
        checkArgument(domainClass.getClass().isAnnotationPresent(Indexed.class));
    }
}
