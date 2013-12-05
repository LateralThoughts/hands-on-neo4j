package com.github.lateralthoughts.hands_on_neo4j.framework.utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsModule {

    @Bean
    //@Singleton
    public DomainUtils provideDomainUtils() {
        return new DomainUtils();
    }

    @Bean
    //@Singleton
    public ClassUtils provideClassUtils() {
        return new ClassUtils();
    }

    @Bean
    //@Singleton
    public CommitUtils provideCommitUtils() {
        return new CommitUtils();
    }
}
