package net.cactusthorn.routing.demo.jetty.dagger;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import net.cactusthorn.routing.demo.jetty.resource.SessionScopeResource;
import dagger.multibindings.ClassKey;

@Module //
public abstract class SessionScopeModule {

    @Binds @IntoMap @NamedScope(SessionScope.class) @ClassKey(SessionScopeResource.class) //
    public abstract Resource bindSessionScopeEntryPoint(SessionScopeResource entryPoint);
}
