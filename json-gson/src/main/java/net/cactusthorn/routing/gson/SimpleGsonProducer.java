package net.cactusthorn.routing.gson;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import net.cactusthorn.routing.producer.Producer;

public class SimpleGsonProducer implements Producer {

    private Gson gson;

    public SimpleGsonProducer(Gson gson) {
        this.gson = gson;
    }

    public SimpleGsonProducer() {
        gson = new GsonBuilder().create();
    }

    @Override //
    public void produce(Object object, String template, String mediaType, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (object == null) {
            if (resp.getStatus() == HttpServletResponse.SC_OK) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            return;
        }
        try (JsonWriter writer = new JsonWriter(resp.getWriter())) {
            gson.toJson(object, object.getClass(), writer);
        }
    }
}
