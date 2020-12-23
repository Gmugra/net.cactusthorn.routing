package net.cactusthorn.routing.converter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.cactusthorn.routing.Template.PathValues;

public interface Converter<T> {

    class RequestData {

        private PathValues pathValues;
        private Map<String, String[]> parameterMap;

        public RequestData(HttpServletRequest request, PathValues pathValues) {
            this.pathValues = pathValues;
            parameterMap = request.getParameterMap();
        }

        public PathValues getPathValues() {
            return pathValues;
        }

        public Map<String, String[]> getParameterMap() {
            return parameterMap;
        }

        public String getParameter(String name) {
            String[] parameters = parameterMap.get(name);
            if (parameters == null || parameters.length == 0) {
                return null;
            }
            return parameters[0];
        }
    }

    T convert(RequestData requestData, Class<?> type, String value);
}
