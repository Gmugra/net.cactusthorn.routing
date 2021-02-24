package net.cactusthorn.routing.delegate;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import net.cactusthorn.routing.util.Language;
import net.cactusthorn.routing.util.Messages;

public class RuntimeDelegateImpl extends RuntimeDelegate {

    private final Map<Class<?>, HeaderDelegate<?>> headerDelegates = new HashMap<>();

    public RuntimeDelegateImpl() {
        headerDelegates.put(MediaType.class, new MediaTypeHeaderDelegate());
        headerDelegates.put(String.class, new StringHeaderDelegate());
        headerDelegates.put(Locale.class, new LocaleHeaderDelegate());
        headerDelegates.put(Date.class, new DateHeaderDelegate());
        headerDelegates.put(URI.class, new UriHeaderDelegate());
        headerDelegates.put(EntityTag.class, new EntityTagHeaderDelegate());
        headerDelegates.put(NewCookie.class, new NewCookieHeaderDelegate());
        headerDelegates.put(CacheControl.class, new CacheControlHeaderDelegate());
        headerDelegates.put(Cookie.class, new CookieHeaderDelegate());
        headerDelegates.put(Language.class, new LanguageHeaderDelegate());
    }

    @Override //
    public UriBuilder createUriBuilder() {
        return new UriBuilderImpl();
    }

    @Override //
    public ResponseBuilder createResponseBuilder() {
        return new ResponseImpl.ResponseBuilderImpl();
    }

    @Override //
    public VariantListBuilder createVariantListBuilder() {
        return new VariantListBuilderImpl();
    }

    @Override //
    public <T> T createEndpoint(Application application, Class<T> endpointType)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override @SuppressWarnings("unchecked") //
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException(Messages.isNull(type));
        }
        return (HeaderDelegate<T>) headerDelegates.get(type);
    }

    @Override //
    public Builder createLinkBuilder() {
        return new LinkImpl.LinkBuilderImpl();
    }

}
