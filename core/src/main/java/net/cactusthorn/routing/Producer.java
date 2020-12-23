package net.cactusthorn.routing;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Producer {

    void produce(Object object, String mediaType, HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
