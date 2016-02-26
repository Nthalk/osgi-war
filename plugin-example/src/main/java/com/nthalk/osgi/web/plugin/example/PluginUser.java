package com.nthalk.osgi.web.plugin.example;

import com.nthalk.osgi.web.core.api.DataSourceService;
import com.nthalk.osgi.web.core.api.DataSourceUnavailableException;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.log4j.Logger;

import javax.sql.DataSource;

@Component(immediate = true)
public class PluginUser {
    private static final Logger LOG = Logger.getLogger(PluginUser.class);

    @Reference
    DataSourceService dataSourceService;

    @Activate
    protected void activate() {
        try {
            DataSource cats = dataSourceService.getDataSource("cats");
            LOG.info("Got datasource: " + cats);
        } catch (DataSourceUnavailableException e) {
            e.printStackTrace();
        }
    }

}
