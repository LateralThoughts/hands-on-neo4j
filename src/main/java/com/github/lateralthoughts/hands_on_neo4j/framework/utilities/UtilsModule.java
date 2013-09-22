package com.github.lateralthoughts.hands_on_neo4j.framework.utilities;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class UtilsModule {

    @Provides
    @Singleton
    public DomainUtils provideDomainUtils() {
        return new DomainUtils();
    }

    @Provides
    @Singleton
    public ClassUtils provideClassUtils() {
        return new ClassUtils();
    }

    @Provides
    @Singleton
    public CommitUtils provideCommitUtils() {
        return new CommitUtils();
    }
}
