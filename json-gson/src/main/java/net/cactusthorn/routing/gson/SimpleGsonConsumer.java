package net.cactusthorn.routing.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RequestData;

public class SimpleGsonConsumer implements Consumer {

    private Gson gson;

    public SimpleGsonConsumer(Gson gson) {
        this.gson = gson;
    }

    public SimpleGsonConsumer() {
        gson = new GsonBuilder().create();
    }

    @Override //
    public Object consume(Class<?> clazz, String mediaType, RequestData data) {
        return gson.fromJson(data.requestBody(), clazz);
    }

}
