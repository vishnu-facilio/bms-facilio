package com.facilio.accounts.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.db.util.DBConf;
import com.facilio.service.DataSourceInterfaceHandler;

public class AppDataSourceImpl implements DataSourceInterfaceHandler {
	
	@Override
	public String getDataSource () {
		Organization org = AccountUtil.getCurrentOrg();
		
		String ds =  null;
		if(org != null) {
			ds = org.getDataSource();
		}
		return (ds != null && !ds.trim().isEmpty())? ds: DBConf.getInstance().getDefaultDataSource();
	}

	@Override
	public String getDbName () {
		Organization org = AccountUtil.getCurrentOrg();
		String db =  null;
		if(org != null) {
			db = org.getDataSource();
		}
		return (db != null && !db.trim().isEmpty())? db:DBConf.getInstance().getDefaultAppDB();
	}
}
