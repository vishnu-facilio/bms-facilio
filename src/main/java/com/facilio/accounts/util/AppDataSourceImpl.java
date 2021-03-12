package com.facilio.accounts.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.db.util.DBConf;
import com.facilio.service.DataSourceInterfaceHandler;

import java.util.Objects;

public class AppDataSourceImpl implements DataSourceInterfaceHandler {

    @Override
    public String getDataSource () {
        Organization org = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(org,"current org is null while fetching data source");
        String ds = org.getDataSource();
        return (ds != null && !ds.trim().isEmpty()) ? ds : DBConf.getInstance().getDefaultDataSource();
    }

    @Override
    public String getDbName () {
        Organization org = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(org,"current org is null while fetching db");
        String db = org.getDbName();
        return (db != null && !db.trim().isEmpty()) ? db : DBConf.getInstance().getDefaultDB();
    }
}
