package net.cactusthorn.routing.demo.jetty.dagger;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Component;
import net.cactusthorn.routing.demo.jetty.service.IAmSingletonService;

@Singleton @Component(modules = { RequestScopeModule.class, SessionBuilderModule.class }) //
public interface Main {

    Map<Class<?>, Provider<EntryPoint>> entryPoints(); //request scope entry points

    Session.Builder sessionBuilder();

    IAmSingletonService iAmSingleton();
}
