package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ApplicationApi {
	public static long addApplicationApi(ApplicationContext application) throws Exception {
		long appId = 0;
		if(application!=null) {
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getApplicationModule().getTableName())
					.fields(FieldFactory.getApplicationFields());
			appId = builder.insert(FieldUtil.getAsProperties(application));
		}
		return appId;
	}
	
	public static List<WebTabGroupContext> getWebTabgroups() throws Exception {
		 GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
	                .table(ModuleFactory.getWebTabGroupModule().getTableName())
	                .select(FieldFactory.getWebTabGroupFields());
		 List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabGroupContext.class);
		return webTabGroups;
	}
	
	public static void updateWebTabGroups(WebTabGroupContext webTabGroup) throws Exception{
		 GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                 .table(ModuleFactory.getWebTabGroupModule().getTableName())
                 .fields(FieldFactory.getWebTabGroupFields())
                 .andCondition(CriteriaAPI.getIdCondition(webTabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
         builder.update(FieldUtil.getAsProperties(webTabGroup));
	}
}
