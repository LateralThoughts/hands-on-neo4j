package com.github.lateralthoughts.hands_on_neo4j.framework.utilities;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

public class ClassUtils {

    /**
     * Returns an equivalent representation of the simple class name
     * with the underscore notation.
     * 
     * E.g.: transforms MyClass into MY_CLASS
     */
    public String toUpperUnderscoreNotation(Class<?> domainClass) {
        return UPPER_CAMEL.to(UPPER_UNDERSCORE, domainClass.getSimpleName());
    }
}
