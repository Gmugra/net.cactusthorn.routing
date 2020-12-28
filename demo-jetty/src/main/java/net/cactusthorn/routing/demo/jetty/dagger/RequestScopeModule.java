package net.cactusthorn.routing.demo.jetty.dagger;

import dagger.*;
import dagger.multibindings.*;
import net.cactusthorn.routing.demo.jetty.entrypoint.*;

@Module //
public abstract class RequestScopeModule {

    @Binds @IntoMap @ClassKey(SimpleEntryPoint.class) //
    public abstract EntryPoint bindSimpleEntryPoint(SimpleEntryPoint entryPoint);

    @Binds @IntoMap @ClassKey(GsonEntryPoint.class) //
    public abstract EntryPoint bindGsonEntryPoint(GsonEntryPoint entryPoint);

    @Binds @IntoMap @ClassKey(HtmlEntryPoint.class) //
    public abstract EntryPoint bindHtmlEntryPoint(HtmlEntryPoint entryPoint);
}
