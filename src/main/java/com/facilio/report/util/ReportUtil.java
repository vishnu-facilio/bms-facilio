package com.facilio.report.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ReportUtil {

	
	public static ReportContext getReport(long reportId) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportId, module))
													;
		
		List<Map<String, Object>> props = select.get();
		if(props != null && !props.isEmpty()) {
			ReportContext report = FieldUtil.getAsBeanFromMap(props.get(0), ReportContext.class);
			return report;
		}
		return null;
	}
	
	public static long addReport(ReportContext reportContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getReportModule().getTableName())
													.fields(FieldFactory.getReport1Fields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
		long id = insertBuilder.insert(props);
		reportContext.setId(id);
		
		return id;
	}
	
	public static void addReportFields(List<ReportFieldContext> reportFieldContexts) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportFieldsModule().getTableName())
				.fields(FieldFactory.getReportFieldsFields());

		List<Map<String, Object>> values = new ArrayList<>();
		
		for(ReportFieldContext reportFieldContext :reportFieldContexts) {
			Map<String, Object> props = FieldUtil.getAsProperties(reportFieldContext);
			values.add(props);
		}
		insertBuilder.addRecords(values);
		insertBuilder.save();
	}
}
