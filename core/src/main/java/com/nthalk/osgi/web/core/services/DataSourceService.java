package com.nthalk.osgi.web.core.services;

import com.nthalk.osgi.web.core.api.DataSourceUnavailableException;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@Component(
    label = "DataSource Service",
    description = "Get DataSources")
@Service
public class DataSourceService implements com.nthalk.osgi.web.core.api.DataSourceService {
    private static final Logger LOG = Logger.getLogger(DataSourceService.class);

    @Override
    public DataSource getDataSource(String name) throws DataSourceUnavailableException {
        try {
            InitialContext initialContext = new InitialContext();
            Object found;
            try {
                found = initialContext.lookup(name);
            } catch (NamingException e) {
                found = initialContext.lookup("java:/comp/env/" + name);
            }

            if (found == null) {
                throw new DataSourceUnavailableException();
            }

            if (found instanceof DataSource) {
                return (DataSource) found;
            }

            throw new DataSourceUnavailableException();
        } catch (NamingException e) {
            throw new DataSourceUnavailableException();
        }
    }

    @Activate
    public void activate() {
        LOG.info("activated");
    }
}
