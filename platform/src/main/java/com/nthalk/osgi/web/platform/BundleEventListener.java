package com.nthalk.osgi.web.platform;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class BundleEventListener implements BundleListener {
    private static final Logger LOG = Logger.getLogger(BundleEventListener.class);

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        int type = bundleEvent.getType();
        String typeName;
        switch (type) {
            case BundleEvent.INSTALLED:
                typeName = "Installed";
                break;
            case BundleEvent.STARTED:
                typeName = "Started";
                break;
            case BundleEvent.STOPPED:
                typeName = "Stopped";
                break;
            case BundleEvent.UPDATED:
                typeName = "Updated";
                break;
            case BundleEvent.UNINSTALLED:
                typeName = "Uninstalled";
                break;
            case BundleEvent.RESOLVED:
                typeName = "Resolved";
                break;
            case BundleEvent.UNRESOLVED:
                typeName = "Unresolved";
                break;
            case BundleEvent.STARTING:
                typeName = "Starting";
                break;
            case BundleEvent.STOPPING:
                typeName = "Stopping";
                break;
            case BundleEvent.LAZY_ACTIVATION:
                typeName = "Lazy activation";
                break;
            default:
                typeName = "Unknown " + type;
        }

        LOG.info(typeName + ": " + bundleEvent.getBundle().getSymbolicName());
    }
}
