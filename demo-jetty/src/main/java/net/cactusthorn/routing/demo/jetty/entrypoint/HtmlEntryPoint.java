package net.cactusthorn.routing.demo.jetty.entrypoint;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.Part;

import net.cactusthorn.routing.Response;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.demo.jetty.dagger.EntryPoint;

@Path("html") //
public class HtmlEntryPoint implements EntryPoint {

    @Inject //
    public HtmlEntryPoint() {
    }

    @GET @Produces("text/html") @Template("/index.html") //
    public String getitHtml() {
        return "TEST HTML PAGE";
    }

    @GET @Path("upload") //
    public Response showUpload() {
        return Response.builder().setContentType("text/html").setTemplate("/fileupload.html").build();
    }

    @POST @Path("doupload") @Consumes("multipart/form-data") //
    public String upload(@FormParam("fname") String fname, @FormPart("myfile") Part part, @FormPart("myfile2") Part part2)
            throws IOException {

        String result = fname + " :: ";

        java.nio.file.Path tmpDir = Files.createTempDirectory("");
        String fileName = part.getSubmittedFileName();
        if (!"".equals(fileName)) {
            java.nio.file.Path path = tmpDir.resolve(fileName);
            Files.copy(part.getInputStream(), path);
            result += path + " :: ";
        }

        String fileName2 = part2.getSubmittedFileName();
        if (!"".equals(fileName)) {
            java.nio.file.Path path = tmpDir.resolve(fileName2);
            Files.copy(part2.getInputStream(), path);
            result += path;
        }

        return result;
    }

    // @formatter:off
    @POST @Path("form") @Consumes("application/x-www-form-urlencoded") //
    public String doHtml(
            @FormParam("fname") String fname,
            @FormParam("lname") String lname, 
            @FormParam("box") List<Integer> box,
            @CookieParam("JSESSIONID") Cookie jsession) {
        return fname + " :: " + lname + " :: " + box + " :: " + jsession.getValue();
    }
    // @formatter:on
}
