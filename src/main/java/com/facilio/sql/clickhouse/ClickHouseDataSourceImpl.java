package com.facilio.sql.clickhouse;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.util.DBConf;
import com.facilio.service.DataSourceInterfaceHandler;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Log4j
public class ClickHouseDataSourceImpl implements DataSourceInterfaceHandler {
    private static final int MAX_LINES_TO_BE_PRINTED = 30;
    @Override
    public String getDataSource () {
        return FacilioProperties.getDefaultClickhouseDataSource();
    }

    @Override
    public String getDbName () {
        Organization org = AccountUtil.getCurrentOrg();
        if (org == null) { //Temp Fix
            FacilioUtil.printTrace(LOGGER, "current org is null while fetching db. This is not supposed to happen.", MAX_LINES_TO_BE_PRINTED);
            return DBConf.getInstance().getDefaultDB();
        }
        else {
            String db = org.getClickhouseDBName();
            return (db != null && !db.trim().isEmpty()) ? db : DBConf.getInstance().getDefaultDB();
        }
    }
}
