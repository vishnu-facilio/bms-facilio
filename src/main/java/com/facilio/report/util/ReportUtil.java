package com.facilio.report.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ReportUtil {

	
	public static ReportContext getReport(Long reportId) throws Exception {
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
		select.select(FieldFactory.getReport1Fields());
		select.table(ModuleFactory.getReportModule().getTableName());
		select.andCustomWhere("ID = ?", reportId);
		
		List<Map<String, Object>> props = select.get();
		
		if(props != null && !props.isEmpty()) {
			
			ReportContext report = FieldUtil.getAsBeanFromMap(props.get(0), ReportContext.class);
			
			return report;
		}
		return null;
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
