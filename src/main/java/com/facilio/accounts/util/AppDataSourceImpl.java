package com.facilio.accounts.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.db.util.DBConf;
import com.facilio.service.DataSourceInterfaceHandler;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;

import java.util.Objects;

@Log4j
public class AppDataSourceImpl implements DataSourceInterfaceHandler {
    private static final int MAX_LINES_TO_BE_PRINTED = 30;
    @Override
    public String getDataSource () {
        Organization org = AccountUtil.getCurrentOrg();
//        Objects.requireNonNull(org,"current org is null while fetching data source");
        if (org == null) { //Temp fix
            FacilioUtil.printTrace(LOGGER, "current org is null while fetching data source. This is not supposed to happen.", MAX_LINES_TO_BE_PRINTED);
            return DBConf.getInstance().getDefaultDataSource();
        }
        else {
            String ds = org.getDataSource();
            return (ds != null && !ds.trim().isEmpty()) ? ds : DBConf.getInstance().getDefaultDataSource();
        }
    }

    @Override
    public String getDbName () {
        Organization org = AccountUtil.getCurrentOrg();
//        Objects.requireNonNull(org,"current org is null while fetching db");
        if (org == null) { //Temp Fix
            FacilioUtil.printTrace(LOGGER, "current org is null while fetching db. This is not supposed to happen.", MAX_LINES_TO_BE_PRINTED);
            return DBConf.getInstance().getDefaultDB();
        }
        else {
            String db = org.getDbName();
            return (db != null && !db.trim().isEmpty()) ? db : DBConf.getInstance().getDefaultDB();
        }
    }
}
