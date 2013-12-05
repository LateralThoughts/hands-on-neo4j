package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinderModule {

    @Bean
    @Qualifier("start")
    public RelationTipFinder provideStartFinder() {
        return RelationTipFinder.startFinder();
    }

    @Bean
    @Qualifier("end")
    public RelationTipFinder provideEndFinder() {
        return RelationTipFinder.endFinder();
    }
}
