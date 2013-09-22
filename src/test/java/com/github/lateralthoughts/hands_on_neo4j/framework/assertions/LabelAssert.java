package com.github.lateralthoughts.hands_on_neo4j.framework.assertions;

import org.assertj.core.api.AbstractAssert;
import org.neo4j.graphdb.Label;

public class LabelAssert extends AbstractAssert<LabelAssert, Label> {

    protected LabelAssert(Label actual) {
        super(actual, LabelAssert.class);
    }

    public static LabelAssert assertThat(Label label) {
        return new LabelAssert(label);
    }

    public LabelAssert hasName(String expectedLabel) {
        isNotNull();

        String actualValue = actual.name();
        if (!actualValue.equals(expectedLabel)) {
            failWithMessage(
                "Expected Labeled to be equal to <%s> but was <%s>",
                expectedLabel,
                actualValue
            );
        }

        return this;
    }
}
