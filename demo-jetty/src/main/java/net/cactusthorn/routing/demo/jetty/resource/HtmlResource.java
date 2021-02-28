package net.cactusthorn.routing.demo.jetty.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.Part;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;

import net.cactusthorn.routing.annotation.FormPart;
import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.body.writer.Templated;
import net.cactusthorn.routing.demo.jetty.dagger.Resource;

@Path("html") //
public class HtmlResource implements Resource {

    @Inject //
    public HtmlResource() {
    }

    @GET @Produces("text/html") @Template("/form.html") //
    public String getitHtml() {
        return "TEST HTML PAGE";
    }

    @GET @Path("manual") //
    public Response manual() {
        return Response.ok("<html><head><meta charset=\"UTF-8\"></head><body><b>Ü TEST</b></body></html>").type(MediaType.TEXT_HTML_TYPE)
                .build();
    }

    @GET @Path("upload") //
    public Response showUpload() {
        Templated templated = new Templated("/fileupload.html");
        return Response.ok(templated).type(MediaType.TEXT_HTML_TYPE).build();
    }


    @POST @Path("doupload") @Consumes("multipart/form-data") //
    // @formatter:off
    public String upload(
            Form form,
            @FormPart("myfile") Part part, 
            @FormPart("myfile2") Part part2) throws IOException {

        String result = form.asMap().getFirst("fname") + " :: ";

        java.nio.file.Path tmpDir = Files.createTempDirectory("");
        String fileName = part.getSubmittedFileName();
        if (!"".equals(fileName)) {
            java.nio.file.Path path = tmpDir.resolve(fileName);
            Files.copy(part.getInputStream(), path);
            result += path + " :: ";
        }

        String fileName2 = part2.getSubmittedFileName();
        if (!"".equals(fileName2)) {
            java.nio.file.Path path = tmpDir.resolve(fileName2);
            Files.copy(part2.getInputStream(), path);
            result += path;
        }

        return result;
    }
    // @formatter:on

    // @formatter:off
    @POST @Path("form") @Consumes("application/x-www-form-urlencoded") //
    public String doHtml(
            @FormParam("fname") String fname,
            @FormParam("box") List<Integer> box,
            @CookieParam("JSESSIONID") String jsession,
            Form form) {
        MultivaluedMap<String, String> map = form.asMap(); 
        return fname + " :: " + map.getFirst("lname") + " :: " + map.get("box") + " :: " + jsession;
    }
    // @formatter:on
}
