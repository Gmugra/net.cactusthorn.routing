package net.cactusthorn.routing;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import net.cactusthorn.routing.Template.PathValues;

public class RequestData {

    private PathValues pathValues;
    private String requestBody;

    public RequestData(HttpServletRequest request, PathValues pathValues) throws IOException {
        this.pathValues = pathValues;
    }

    public RequestData(HttpServletRequest request, PathValues pathValues, int readBodyBufferSize) throws IOException {
        this.pathValues = pathValues;
        requestBody = requestBody(request, readBodyBufferSize);
    }

    public PathValues pathValues() {
        return pathValues;
    }

    public String requestBody() {
        return requestBody;
    }

    private String requestBody(HttpServletRequest request, int readBodyBufferSize) throws IOException {

        try (Reader reader = request.getReader()) {

            char[] charBuffer = new char[readBodyBufferSize];
            StringBuilder builder = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = reader.read(charBuffer, 0, charBuffer.length)) != -1) {
                builder.append(charBuffer, 0, numCharsRead);
            }
            return builder.toString();
        }
    }
}
