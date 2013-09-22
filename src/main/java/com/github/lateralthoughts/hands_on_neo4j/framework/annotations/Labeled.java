package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target(TYPE)
@Retention(RUNTIME)
/**
 * Declares one or more labels on a {link org.neo4j.graphdb.Node} instance.
 */
public @interface Labeled {

    String[] value() default {};
}
