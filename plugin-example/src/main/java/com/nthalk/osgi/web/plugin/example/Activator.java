package com.nthalk.osgi.web.plugin.example;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    private final Logger LOG = Logger.getLogger(Activator.class);

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Started plugin");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopped plugin");
    }
}
