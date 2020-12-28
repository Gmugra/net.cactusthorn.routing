package net.cactusthorn.routing.demo.jetty.entrypoint;

import javax.inject.Inject;

import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.demo.jetty.dagger.EntryPoint;

@Path("rest/api/gson") //
public class GsonEntryPoint implements EntryPoint {

    @Inject //
    public GsonEntryPoint() {
    }

    @GET @Produces("application/json") //
    public DataObject doitGson() {
        return new DataObject("The Name \u00DF", 123);
    }

    @POST @Consumes("application/json") //
    public String getitGson(@Context DataObject data, @HeaderParam("Accept-Encoding") String acceptEncoding) {
        return data.getName() + "; header : " + acceptEncoding;
    }
}
