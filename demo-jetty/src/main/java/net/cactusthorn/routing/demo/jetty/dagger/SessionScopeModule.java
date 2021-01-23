package net.cactusthorn.routing.demo.jetty.dagger;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.ClassKey;

import net.cactusthorn.routing.demo.jetty.entrypoint.SessionScopeEntryPoint;

@Module //
public abstract class SessionScopeModule {

    @Binds @IntoMap @NamedScope(SessionScope.class) @ClassKey(SessionScopeEntryPoint.class) //
    public abstract EntryPoint bindSessionScopeEntryPoint(SessionScopeEntryPoint entryPoint);
}
