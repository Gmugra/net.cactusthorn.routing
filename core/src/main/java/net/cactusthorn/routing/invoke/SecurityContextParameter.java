package net.cactusthorn.routing.invoke;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.SecurityContext;

import net.cactusthorn.routing.PathTemplate.PathValues;

public class SecurityContextParameter implements MethodParameter {

    static final class SimpleSecurityContext implements SecurityContext {

        private HttpServletRequest request;

        SimpleSecurityContext(HttpServletRequest request) {
            this.request = request;
        }

        @Override //
        public Principal getUserPrincipal() {
            return request.getUserPrincipal();
        }

        @Override //
        public boolean isUserInRole(String role) {
            return request.isUserInRole(role);
        }

        @Override //
        public boolean isSecure() {
            return request.isSecure();
        }

        @Override //
        public String getAuthenticationScheme() {
            return request.getAuthType();
        }
    }

    @Override //
    public SecurityContext findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return new SimpleSecurityContext(req);
    }
}
