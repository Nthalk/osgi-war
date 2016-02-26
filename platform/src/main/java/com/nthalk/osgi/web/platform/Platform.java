package com.nthalk.osgi.web.platform;

import org.apache.commons.io.FileUtils;
import org.apache.felix.fileinstall.internal.DirectoryWatcher;
import org.apache.felix.framework.Felix;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Platform implements ServletContextListener {
    private static final Logger LOG = Logger.getLogger(Platform.class);
    Felix felix;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        Properties systemProperties = System.getProperties();
        String homePath = systemProperties.getProperty("home");
        configureLogging(homePath);
        validateHomePath(homePath);

        Properties properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Could not load version.properties");
        }

        setupPlatformDirectories(homePath, properties);
        properties.setProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "org.apache.log4j;version=" + properties.getProperty("log4j.version"));
        properties.putAll(systemProperties);


        LOG.info("CATS Platform " + properties.getProperty("version"));
        LOG.info("=================");
        try {
            felix = new Felix(properties);
            LOG.info("Initializing...");
            felix.init();

            // Add listener
            BundleContext bundleContext = felix.getBundleContext();
            bundleContext.addBundleListener(new BundleEventListener());

            felix.start();

            LOG.info("Loading platform bundles...");

            // Compendium
            bundleContext.installBundle("file:" + servletContext.getRealPath("WEB-INF/lib/org.osgi.compendium-1.4.0.jar")).start();

            // SCR Runtime
            bundleContext.installBundle("file:" + servletContext.getRealPath("WEB-INF/lib/org.apache.felix.scr-2.0.2.jar")).start();

            // Core bundle
            bundleContext.installBundle("file:" + servletContext.getRealPath("WEB-INF/lib/core-" + properties.getProperty("version") + ".jar")).start();
            // Plugin Example
            bundleContext.installBundle("file:" + servletContext.getRealPath("WEB-INF/lib/plugin-example-" + properties.getProperty("version") + ".jar")).start();

            // Install fileinstall for hotloading plugins
            bundleContext.installBundle("file:" + servletContext.getRealPath("WEB-INF/lib/org.apache.felix.fileinstall-3.5.2.jar")).start();

        } catch (Exception ex) {
            LOG.error("Could not create framework", ex);
        }
    }

    private void validateHomePath(String homePath) {
        if (homePath == null) {
            throw new IllegalArgumentException("System property home should be defined");
        }

        if (!new File(homePath).isDirectory()) {
            throw new IllegalArgumentException("System property home should be a directory, however, '" + homePath + "' is not a directory");
        }
    }

    private void configureLogging(String homePath) {
        System.getProperties().setProperty("org.ops4j.pax.logging.DefaultServiceLog.level", "WARN");
        System.getProperties().setProperty("log4j.ignoreTCL", "true");
        String specifiedLoggingConfiguration = homePath + "/config/log4j.properties";
        if (new File(specifiedLoggingConfiguration).exists()) {
            PropertyConfigurator.configure(specifiedLoggingConfiguration);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            System.out.println("Shutting down...");
            felix.stop();
            felix.waitForStop(30000);
        } catch (Exception e) {
            LOG.error("Could not shutdown", e);
        }
    }

    public void setupPlatformDirectories(String homePath, Properties properties) {
        properties.setProperty(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        properties.setProperty(Constants.FRAMEWORK_STORAGE, homePath + "/cache");
        properties.setProperty(DirectoryWatcher.DIR, homePath + "/modules");
        try {
            FileUtils.deleteDirectory(new File(homePath + "/cache"));
        } catch (IOException ignored) {
        }
        properties.setProperty("felix.cm.dir", homePath + "/config");
    }
}
