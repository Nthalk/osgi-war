package com.nthalk.osgi.web.platform;

import org.apache.commons.io.FileUtils;
import org.apache.felix.fileinstall.internal.DirectoryWatcher;
import org.apache.felix.framework.Felix;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Platform implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(Platform.class);
    private Felix felix;

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
        properties.setProperty(
            Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
            "org.apache.log4j;version=" + properties.getProperty("log4j.version") +
                ",javax.servlet;javax.servlet.http;version=3.0.1");
        properties.putAll(systemProperties);


        LOG.info("CATS Platform " + properties.getProperty("version"));
        LOG.info("=================");
        try {
            felix = new Felix(properties);
            LOG.info("Initializing...");
            felix.init();


            // Add listener
            BundleContext bundleContext = felix.getBundleContext();
            Installer installer = new Installer(bundleContext, servletContext);
            bundleContext.addBundleListener(new BundleEventListener());

            felix.start();

            LOG.info("Loading platform bundles...");

            // Http Servlet proxy setup
            servletContext.setAttribute("org.osgi.framework.BundleContext", bundleContext);

            // Logging
            installer.installWarLib("org.apache.felix.log-1.0.1.jar");

            // Compendium
            installer.installWarLib("org.osgi.compendium-1.4.0.jar");

            // Http Servlet bridge
            installer.installWarLib("org.apache.felix.http.api-2.2.2.jar");
            installer.installWarLib("org.apache.felix.http.bridge-2.2.2.jar");

            // SCR Runtime
            installer.installWarLib("org.apache.felix.scr-2.0.2.jar");

            // WebConsole
            installer.installWarLib("json-20160212.jar");
            installer.installWarLib("commons-io-2.4.jar");
            installer.installWarLib("portlet-api-2.0.jar");
            installer.installWarLib("org.apache.commons.fileupload-1.2.2.LIFERAY-PATCHED-1.jar");
            installer.installWarLib("org.apache.felix.webconsole-4.2.14.jar");

            // Core bundle
            installer.installWarLib("core-" + properties.getProperty("version") + ".jar");
            // Plugin Example
            installer.installWarLib("plugin-example-" + properties.getProperty("version") + ".jar");

            // Install fileinstall for hotloading plugins
            installer.installWarLib("org.apache.felix.fileinstall-3.5.2.jar");

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
            if (felix.getState() == Bundle.ACTIVE) {
                felix.stop();
                felix.waitForStop(30000);
            }
        } catch (Exception e) {
            LOG.error("Could not shutdown", e);
        }
    }

    private void setupPlatformDirectories(String homePath, Properties properties) {
        properties.setProperty(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        properties.setProperty(Constants.FRAMEWORK_STORAGE, homePath + "/cache");
        properties.setProperty(DirectoryWatcher.DIR, homePath + "/modules");
        try {
            FileUtils.deleteDirectory(new File(homePath + "/cache"));
        } catch (IOException ignored) {
        }
        properties.setProperty("felix.cm.dir", homePath + "/config");
    }

    private class Installer {

        private final BundleContext bundleContext;
        private final ServletContext servletContext;

        Installer(BundleContext bundleContext, ServletContext servletContext) {
            this.bundleContext = bundleContext;
            this.servletContext = servletContext;
        }

        void installWarLib(String warLibBundle) throws BundleException {
            bundleContext.installBundle("file:" + servletContext.getRealPath("WEB-INF/lib/" + warLibBundle)).start();
        }
    }
}
