package com.nthalk.osgi.web.core;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(BundleContext bundleContext) throws Exception {
        Logger.getLogger(Activator.class).info("Core started");
    }

    public void stop(BundleContext bundleContext) throws Exception {

    }
}
