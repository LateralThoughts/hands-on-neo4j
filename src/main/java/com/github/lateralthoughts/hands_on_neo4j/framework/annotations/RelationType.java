package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface RelationType {

    /**
     * Relation start
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    public static @interface Start {

    }

    /**
     * Relation end
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    public static @interface End {

    }

    /**
     * Relationship name.
     * Ex.:
     * <pre>
     *      <code>
     *          @RelationType("HAS_COMPLETED_COURSE")
     *          public class CourseCompletion {
     *              // [...]
     *          }
     *      </code>
     * </pre>
     * would result to the following Cypher representation
     * <pre>
     *     <code>
     *          (anyNode)-[:HAS_COMPLETED_COURSE]-(anyNode)
     *     </code>
     * </pre>
     *
     * If no value is specified, the relationship name will be
     * inferred from the class name.
     */
    String value() default "";

    /**
     * Is relationship directed.
     * Ex.:
     * <pre>
     *      <code>
     *          @RelationType(directed = true)
     *          public class Branch {
     *              @Start
     *              private Project project;
     *              @End
     *              private Commit commit;
     *              // [...]
     *          }
     *      </code>
     * </pre>
     * would result to the following Cypher representation:
     * <pre>
     *     <code>
     *          (project)-[:BRANCH]->(commit)
     *     </code>
     * </pre>
     */
    boolean directed() default false;
}
