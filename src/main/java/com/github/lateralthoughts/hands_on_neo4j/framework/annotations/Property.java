package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
/**
 * Declares a {link org.neo4j.graphdb.Node} / {link org.neo4j.graphdb.Relationship} property.
 */
public @interface Property {

    String value() default "";
}
