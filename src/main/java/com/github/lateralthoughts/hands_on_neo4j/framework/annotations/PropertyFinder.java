package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;
import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import static com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry.asKeys;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableList.Builder;
import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

/**
 * Utility class responsible for Neo4J property resolution.
 *
 * Its auxiliary goal is to reduce boilerplate code related to property declaration
 * and reconcile, as its sibling {@link LabelFinder}, domain classes with Neo4J
 * structure-free Neo4J entities.
 */
@Component
public class PropertyFinder {

    private final DomainUtils domainUtils;
    private final Class<? extends Annotation> annotationClass;

    @Autowired
    public PropertyFinder(DomainUtils domainUtils) {
        this(domainUtils, Property.class);
    }

    PropertyFinder(DomainUtils domainUtils, 
                   Class<? extends Annotation> annotationClass) {
        this.domainUtils = domainUtils;
        this.annotationClass = annotationClass;
        checkState(domainUtils != null);
        checkState(
            isSupported(annotationClass),
            "Annotation class should be @Property or annotated with @Property"
        );
    }

    /**
     * Determines the Node/Relationship properties from the provided non-null domain class.
     * If no value is provided, the relationship name is inferred from the class name.
     *
     * @throws NullPointerException if entity is null
     * @throws IllegalArgumentException if entity is not adequately annotated
     * @throws IllegalArgumentException if a property name is declared more than once within the entity
     *
     * @return an *immutable* collection of the actual properties
     */
    public Collection<Entry<String,Object>> findAll(Domain entity) {
        domainUtils.checkIsAnnotatedDomain(entity.getClass());

        Builder<Entry<String,Object>> result = builder();

        try {
            for (Field field : filterAnnotatedFields(entity)) {
                String key = computeKey(entity, result, field);
                result.add(new Entry<> (key, field.get(entity)));
            }
        } catch (IllegalAccessException
            | NoSuchMethodException
            | InvocationTargetException e) {
            throw propagate(e);
        }

        return result.build();
    }

    public Entry<String, Object> findSingle(Domain entity) {
        Collection<Field> fields = filterAnnotatedFields(entity);
        checkArgument(fields.size() == 1);
        try {
            Field field = fields.iterator().next();
            String key = getPropertyNameOrDefault(field);
            return new Entry<>(key, field.get(entity));
        }
        catch (IllegalAccessException
            | NoSuchMethodException
            | InvocationTargetException e) {
            throw propagate(e);
        }
    }

    String getPropertyNameOrDefault(Field field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        field.setAccessible(true);
        String propertyName = getPropertyName(field);
        if (!propertyName.isEmpty()) {
            return propertyName;
        }
        return field.getName();
    }

    private boolean isSupported(Class<? extends Annotation> annotationClass) {
        return annotationClass.equals(Property.class)
            || annotationClass.isAnnotationPresent(Property.class);
    }

    private Collection<Field> filterAnnotatedFields(Domain entity) {
        return filter(
            newArrayList(entity.getClass().getDeclaredFields()),
            annotatedPropertiesPredicate()
        );
    }

    private String computeKey(Domain entity,
                              Builder<Entry<String, Object>> properties,
                              Field field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String key = getPropertyNameOrDefault(field);
        checkNoDuplicates(transform(properties.build(), asKeys()), key, entity);
        return key;
    }

    private String getPropertyName(Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Annotation annotation = field.getAnnotation(annotationClass);
        return annotationClass.getMethod("value").invoke(annotation).toString();
    }

    private void checkNoDuplicates(Collection<String> keys, String key, Domain entity) {
        if (keys.contains(key)) {
            throw new IllegalArgumentException(format(
                "Domain class %s declares at least twice property name \"%s\"",
                entity.getClass().getSimpleName(),
                key
            ));
        }
    }

    private Predicate<Field> annotatedPropertiesPredicate() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return field.isAnnotationPresent(Property.class);
            }
        };
    }
}
