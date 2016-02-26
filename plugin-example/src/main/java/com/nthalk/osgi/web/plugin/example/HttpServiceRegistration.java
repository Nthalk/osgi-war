package com.nthalk.osgi.web.plugin.example;

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
public class HttpServiceRegistration extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(HttpServiceRegistration.class);

    @Reference
    HttpService httpService;

    @Activate
    protected void activate() {
        try {
            httpService.registerServlet("/yay", this, null, null);
        } catch (ServletException | NamespaceException e) {
            LOG.error("Could not register servlet", e);
        }
    }

    @Deactivate
    protected void deactivate() {
        httpService.unregister("/yay");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();

        out.println("Request = " + req);
        out.println("PathInfo = " + req.getPathInfo());
    }

}
