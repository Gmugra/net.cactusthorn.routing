package net.cactusthorn.routing.demo.jetty.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.HeaderParam;

import net.cactusthorn.routing.demo.jetty.dagger.Resource;

@Path("rest/api/gson") //
public class GsonResource implements Resource {

    @Inject //
    public GsonResource() {
    }

    @GET @Produces(MediaType.APPLICATION_JSON) //
    public DataObject doitGson() {
        return new DataObject("The Name \u00DF", 123);
    }

    @POST @Consumes(MediaType.APPLICATION_JSON) //
    public String getitGson(DataObject data, @HeaderParam("Accept-Encoding") String acceptEncoding) {
        return data.getName() + "; header : " + acceptEncoding;
    }
}
