package com.github.lateralthoughts.hands_on_neo4j.framework.functional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.google.common.base.Predicate;

public final class AnnotatedFieldPredicate implements Predicate<Field> {

    private final Class<? extends Annotation> annotationClass;

    private AnnotatedFieldPredicate(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public static AnnotatedFieldPredicate ANNOTATED_FIELD(Class<? extends Annotation> annotationClass) {
        return new AnnotatedFieldPredicate(annotationClass);
    }

    @Override
    public boolean apply(Field input) {
        input.setAccessible(true);
        return input.isAnnotationPresent(annotationClass);
    }
}
