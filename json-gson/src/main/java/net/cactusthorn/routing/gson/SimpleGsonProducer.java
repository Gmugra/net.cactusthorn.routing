package net.cactusthorn.routing.gson;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import net.cactusthorn.routing.Producer;

public class SimpleGsonProducer implements Producer {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleGsonProducer.class);

    private Gson gson;

    public SimpleGsonProducer(Gson gson) {
        this.gson = gson;
    }

    public SimpleGsonProducer() {
        gson = new GsonBuilder().create();
    }

    @Override //
    public void produce(Object object, String mediaType, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (object == null) {
            LOG.warn("Entry point return Object is NULL");
        }
        try (JsonWriter writer = new JsonWriter(resp.getWriter())) {
            gson.toJson(object, object.getClass(), writer);
        }
    }
}
