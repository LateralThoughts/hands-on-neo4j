package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.inject.Inject;

import com.github.lateralthoughts.hands_on_neo4j.domain.Domain;
import com.github.lateralthoughts.hands_on_neo4j.framework.datastructures.Entry;
import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.DomainUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public class UniqueIdentifierFinder {

    private final DomainUtils domainUtils;
    private final PropertyFinder propertyFinder;

    @Inject
    public UniqueIdentifierFinder(DomainUtils domainUtils) {
        this.domainUtils = domainUtils;
        this.propertyFinder = new PropertyFinder(domainUtils, UniqueIdentifier.class);
    }
    
    public Entry<String, Object> findSingleKeyValue(Domain domainInstance) {
        checkNotNull(domainInstance);

        try {
            Field identityField = getUniqueIdentifier(domainInstance.getClass());
            String propertyName = propertyFinder.getPropertyNameOrDefault(identityField);
            return new Entry<>(propertyName, identityField.get(domainInstance));
        } catch (IllegalAccessException
            | NoSuchMethodException
            | InvocationTargetException e) {
            throw propagate(e);
        }
    }

    public String findSingleKey(Class<? extends Domain> domainClass) {
        checkNotNull(domainClass);

        try {
            Field identityField = getUniqueIdentifier(domainClass);
            return propertyFinder.getPropertyNameOrDefault(identityField);
        } catch (IllegalAccessException
            | NoSuchMethodException
            | InvocationTargetException e) {
            throw propagate(e);
        }
    }

    private Field getUniqueIdentifier(Class<? extends Domain> domainClass) {
        checkNotNull(domainClass);
        domainUtils.checkIsAnnotatedDomain(domainClass);

        Collection<Field> fields = filter(newArrayList(domainClass.getDeclaredFields()), annotatedUniquePropertyPredicate());
        checkOnlyOnePropertyIsAnnotated(domainClass, fields);
        return fields.iterator().next();
    }

    private void checkOnlyOnePropertyIsAnnotated(Class<? extends Domain> domainClass, Collection<Field> fields) {
        String simpleClassName = domainClass.getSimpleName();
        if (fields.size() == 0) {
            throw new IllegalArgumentException(String.format(
                "Domain class %s does not declares any @UniqueIdentifier property",
                simpleClassName
            ));
        }
        if (fields.size() > 1) {
            throw new IllegalArgumentException(String.format(
                "Domain class %s declares too many @UniqueIdentifier property: %s",
                simpleClassName,
                Joiner.on(", ").skipNulls().join(convertFields(fields))
            ));
        }
    }

    private Predicate<Field> annotatedUniquePropertyPredicate() {
        return new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return field.isAnnotationPresent(UniqueIdentifier.class);
            }
        };
    }

    private ImmutableList<String> convertFields(Collection<Field> fields) {
        return FluentIterable
            .from(fields)
            .transform(new Function<Field, String>() {
                @Override
                public String apply(Field input) {
                    if (input == null) {
                        return null;
                    }
                    return input.getName();
                }
            })
            .toList();
    }
}
