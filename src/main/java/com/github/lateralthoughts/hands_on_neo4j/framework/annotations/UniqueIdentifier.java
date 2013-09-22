package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
@Property
/**
 * Similar to {link Property}, this annotation specifies a single identity property.
 */
public @interface UniqueIdentifier {
    String value() default "";
}
