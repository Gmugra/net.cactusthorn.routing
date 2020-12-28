package net.cactusthorn.routing.demo.jetty.dagger;

import dagger.*;
import dagger.multibindings.*;
import net.cactusthorn.routing.demo.jetty.entrypoint.SessionScopeEntryPoint;

@Module //
public abstract class SessionScopeModule {

    @Binds @IntoMap @NamedScope(SessionScope.class) @ClassKey(SessionScopeEntryPoint.class) //
    public abstract EntryPoint bindSessionScopeEntryPoint(SessionScopeEntryPoint entryPoint);
}
