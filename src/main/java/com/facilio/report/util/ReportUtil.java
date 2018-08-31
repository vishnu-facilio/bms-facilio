package com.facilio.report.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.sql.GenericInsertRecordBuilder;

public class ReportUtil {

	
	public static void getReport(Long reportId) {
		
		
	}
	
	public static void addReport(ReportContext reportContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportModule().getTableName())
				.fields(FieldFactory.getReport1Fields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		reportContext.setId((long)props.get("id"));
	}
}
