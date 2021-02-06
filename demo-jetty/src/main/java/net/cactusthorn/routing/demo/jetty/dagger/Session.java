package net.cactusthorn.routing.demo.jetty.dagger;

import dagger.Subcomponent;

import java.util.Map;

import javax.inject.Provider;

@SessionScope @Subcomponent(modules = { SessionScopeModule.class }) //
public interface Session {

    @NamedScope(SessionScope.class) //
    Map<Class<?>, Provider<Resource>> resources(); // session scope entry points

    @Subcomponent.Builder //
    interface Builder {
        Session build();
    }
}
