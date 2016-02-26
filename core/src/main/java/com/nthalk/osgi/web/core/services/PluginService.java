package com.nthalk.osgi.web.core.services;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;

@Component(
    label = "Plugin Service",
    description = "Information about plugins")
@Service
public class PluginService implements com.nthalk.osgi.web.core.api.PluginService {
    private static final Logger LOG = Logger.getLogger(PluginService.class);

    @Override
    public void doTheThing() {
        LOG.info("did the thing");
    }

    @Activate
    public void activate() {
        LOG.info("activated");
    }
}
