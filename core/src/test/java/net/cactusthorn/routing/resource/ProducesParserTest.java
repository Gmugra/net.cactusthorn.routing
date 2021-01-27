package net.cactusthorn.routing.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

public class ProducesParserTest {

    @Path("/") //
    public static class EntryPoint1 {

        @POST //
        public void text() {
        }
    }

    @Path("/") @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML }) //
    public static class EntryPoint2 {

        @POST //
        public void text() {
        }
    }

    @Test //
    public void defaultByClass() {
        ProducesParser parser = new ProducesParser();
        List<MediaType> mediaTypes = parser.produces(EntryPoint1.class);
        assertEquals(MediaType.TEXT_PLAIN_TYPE, mediaTypes.get(0));
    }

    @Test //
    public void byClass() {
        ProducesParser parser = new ProducesParser();
        List<MediaType> mediaTypes = parser.produces(EntryPoint2.class);
        assertEquals(MediaType.APPLICATION_JSON_TYPE, mediaTypes.get(0));
        assertEquals(MediaType.TEXT_HTML_TYPE, mediaTypes.get(1));
    }

}
