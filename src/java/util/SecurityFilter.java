/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import managedbean.UserLogin;

/**
 *
 * @author morgan
 */
@WebFilter("/*")
public class SecurityFilter implements Filter {
//public static final String POLICY = "default-src 'self'";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
         try {

            if (!(request instanceof HttpServletRequest)) {
                chain.doFilter(request, response);
                return;
            }

            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = ((HttpServletResponse) response);
            if (req.isRequestedSessionIdFromURL()) {
                HttpSession session = req.getSession();
                if (session != null) {
                    session.invalidate();
                }

            } else {
                String contextual = req.getServletContext().getContextPath() + "/index.xhtml";

                res.addHeader("X-Content-Type-Options", "nosniff");
                res.addHeader("x-xss-protection", "1;mode=block");
                //res.setHeader("Content-Security-Policy", POLICY);

                String url = req.getRequestURI();
                boolean is_resource = url.contains("javax.faces.resource") || url.contains("error") || url.contains("img") || url.contains("push");
                if (!url.contains("documentviewer")) {
                    res.addHeader("X-Frame-Options", "DENY");
                }

                UserLogin session = (UserLogin) req.getSession().getAttribute("login");

                if (session == null || session.isLogged_in() == false) {

                    if (url.contains("index") || is_resource) {

                        chain.doFilter(request, new SecureCookieSetter(res));

                    } else {
                       
                        res.sendRedirect(contextual);
                    }

                } else//logged in now
                 if (!url.contains("ome")&& !is_resource) {//any other page apart from home direct to login
                        HttpSession vs = req.getSession();
                        if (vs != null) {
                            vs.invalidate();
                        }
                        res.sendRedirect(contextual);
                    } else {
                        chain.doFilter(request, new SecureCookieSetter(res));
                    }

            }
        } catch (IOException | ServletException t) {
            t.printStackTrace();
        }
    }

    @Override
    public void destroy() {
       
    }

   
       class SecureCookieSetter extends HttpServletResponseWrapper {

        public SecureCookieSetter(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addCookie(Cookie cookie) {
            cookie.setSecure(true);
            super.addCookie(cookie);
        }

        @Override
        public void addHeader(String name, String value) {
            if ((name.equals("Set-Cookie")) && (!value.matches("(^|.*;)\\s*Secure"))) {
                value = value + ";Secure";
            }
            super.addHeader(name, value);
        }

        @Override
        public void setHeader(String name, String value) {
            if ((name.equals("Set-Cookie")) && (!value.matches("(^|.*;)\\s*Secure"))) {
                value = value + ";Secure";
            }
            super.setHeader(name, value);
        }

    }
}
