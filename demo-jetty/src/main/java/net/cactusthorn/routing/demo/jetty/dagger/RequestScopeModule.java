package net.cactusthorn.routing.demo.jetty.dagger;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import net.cactusthorn.routing.demo.jetty.resource.*;

@Module //
public abstract class RequestScopeModule {

    @Binds @IntoMap @ClassKey(SimpleResource.class) //
    public abstract Resource bindSimpleEntryPoint(SimpleResource entryPoint);

    @Binds @IntoMap @ClassKey(GsonResource.class) //
    public abstract Resource bindGsonEntryPoint(GsonResource entryPoint);

    @Binds @IntoMap @ClassKey(HtmlResource.class) //
    public abstract Resource bindHtmlEntryPoint(HtmlResource entryPoint);
}
