package com.github.lateralthoughts.hands_on_neo4j.framework.utilities;

import java.util.UUID;

public class CommitUtils {

    public String uniqueIdentifier() {
        return UUID.randomUUID().toString();
    }
}
