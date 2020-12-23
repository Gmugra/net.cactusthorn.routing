package net.cactusthorn.routing;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import net.cactusthorn.routing.Template.PathValues;

public class RequestData {

    private PathValues pathValues;
    private String requestBody;

    public RequestData(HttpServletRequest request, PathValues pathValues) throws IOException {
        this(request, pathValues, false);
    }

    public RequestData(HttpServletRequest request, PathValues pathValues, boolean readBody) throws IOException {
        this.pathValues = pathValues;
        if (readBody) {
            requestBody = requestBody(request);
        }
    }

    public PathValues pathValues() {
        return pathValues;
    }

    public String requestBody() {
        return requestBody;
    }

    private String requestBody(HttpServletRequest request) throws IOException {

        try (Reader reader = request.getReader()) {

            char[] charBuffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = reader.read(charBuffer, 0, charBuffer.length)) != -1) {
                builder.append(charBuffer, 0, numCharsRead);
            }
            return builder.toString();
        }
    }
}
