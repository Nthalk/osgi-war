package com.nthalk.osgi.web.core;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.log4j.Logger;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component(immediate = true)
public class HttpServletRegistration extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(HttpServletRegistration.class);

    private static final String PATH = "/";

    @Reference
    HttpService httpService;

    @Activate
    public void activate() {
        try {
            httpService.registerServlet(PATH, this, null, null);
        } catch (ServletException | NamespaceException e) {
            LOG.error("Could not register servlet", e);
        }
    }

    @Deactivate
    public void deactivate() {
        httpService.unregister(PATH);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.println("Core: " + req.getPathInfo());
    }
}
