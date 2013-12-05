package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
/**
 * Declare the Neo4J index name reserved for the annotated type.
 */
public @interface Indexed {

    String value() default "";
}
