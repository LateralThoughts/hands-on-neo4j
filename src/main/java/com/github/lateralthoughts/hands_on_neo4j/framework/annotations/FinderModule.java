package com.github.lateralthoughts.hands_on_neo4j.framework.annotations;

import javax.inject.Named;

import com.github.lateralthoughts.hands_on_neo4j.framework.utilities.UtilsModule;

import dagger.Module;
import dagger.Provides;

@Module(
    library = true,
    includes = UtilsModule.class
)
public class FinderModule {

    @Provides
    @Named("start")
    public RelationTipFinder provideStartFinder() {
        return RelationTipFinder.startFinder();
    }

    @Provides
    @Named("end")
    public RelationTipFinder provideEndFinder() {
        return RelationTipFinder.endFinder();
    }
}
