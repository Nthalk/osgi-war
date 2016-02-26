package com.nthalk.osgi.web.core.api;

import javax.sql.DataSource;

public interface DataSourceService {
    DataSource getDataSource(String name) throws DataSourceUnavailableException;
}
