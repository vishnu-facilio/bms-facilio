package com.facilio.report.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReportAxisContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.context.ReportXCriteriaContext;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ReportUtil {
	
	public static List<ReportFolderContext> getAllReportFolder(String moduleName,boolean isWithReports) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioModule reportFoldermodule = ModuleFactory.getReportFolderModule();
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1FolderFields())
													.table(reportFoldermodule.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportFoldermodule))
													.andCustomWhere(reportFoldermodule.getTableName()+".MODULEID = ?",module.getModuleId())
													;
		
		List<Map<String, Object>> props = select.get();
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				
				ReportFolderContext folder = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
				if(isWithReports) {
					List<ReportContext> reports = getReportsFromFolderId(folder.getId());
					folder.setReports(reports);
				}
				reportFolders.add(folder);
			}
		}
		return reportFolders;
	}
	
	public static void moveReport(ReportContext reportContext, long folderId) throws Exception {
		FacilioModule module = ModuleFactory.getReportModule();
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List< FacilioField> updateFields = modbean.getAllFields(module.getName());
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName()).andCustomWhere("WHERE ID = ?", reportContext.getId()).fields(updateFields);
		
		Map<String,Object> prop = FieldUtil.getAsProperties(reportContext);
		updateBuilder.update(prop);
		
	}
	public static List<ReportContext> getReportsFromFolderId(long folderId) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCustomWhere(module.getTableName()+".REPORT_FOLDER_ID = ?",folderId)
													;
		
		List<Map<String, Object>> props = select.get();
		List<ReportContext> reports = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				
				ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				reports.add(report);
			}
		}
		return reports;
	}
	
	public static ReportContext getReport(long reportId) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportId, module))
													;
		
		List<ReportContext> reports = getReportsFromProps(select.get());
		if (reports != null && !reports.isEmpty()) {
			return reports.get(0);
		}
		return null;
	}
	
	private static List<ReportContext> getReportsFromProps(List<Map<String, Object>> props) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<ReportContext> reports = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map<String, Object> prop : props) {
				ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				
				if (report.getxCriteria() != null) {
					ReportXCriteriaContext xCriteria = report.getxCriteria();
					xCriteria.setxField(getField(xCriteria.getxFieldId(), xCriteria.getModuleName(), xCriteria.getxFieldName(), modBean));
				}
				
				for (ReportDataPointContext dataPoint : report.getDataPoints()) {
					ReportAxisContext xAxis = dataPoint.getxAxis();
					xAxis.setField(getField(xAxis.getFieldId(), xAxis.getModuleName(), xAxis.getFieldName(), modBean));
					
					ReportAxisContext yAxis = dataPoint.getyAxis();
					yAxis.setField(getField(yAxis.getFieldId(), yAxis.getModuleName(), yAxis.getFieldName(), modBean));
					
					dataPoint.setDateField(getField(dataPoint.getDateFieldId(), dataPoint.getDateFieldModuleName(), dataPoint.getDateFieldName(), modBean));
				}
				
				reports.add(report);
			}
			return reports;
		}
		return null;
	}
	
	private static FacilioField getField(long fieldId, String moduleName, String fieldName, ModuleBean modBean) throws Exception {
		if (fieldId != -1) {
			return modBean.getField(fieldId);
		}
		else if (moduleName != null && !moduleName.isEmpty() && fieldName != null && !fieldName.isEmpty()) {
			return modBean.getField(fieldName, moduleName);
		}
		return null;
	}
	
	public static List<Map<String, Object>> getReportFromFolderId(long reportFolderId) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCustomWhere("REPORT_FOLDER_ID = ?", reportFolderId);
													;
		
		List<Map<String, Object>> props = select.get();
		return props;
	}
	
	public static ReportFolderContext addReportFolder(ReportFolderContext reportFolderContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.fields(FieldFactory.getReport1FolderFields());

		reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		Map<String, Object> props = FieldUtil.getAsProperties(reportFolderContext);
		long id = insertBuilder.insert(props);
		reportFolderContext.setId(id);
		
		return reportFolderContext;
	}
	
	public static ReportFolderContext updateReportFolder(ReportFolderContext reportFolderContext) throws Exception {
		
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.fields(FieldFactory.getReport1FolderFields())
				.andCustomWhere("ID = ?", reportFolderContext.getId());
		
		reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportFolderContext);
		update.update(props);
		
		return reportFolderContext;
	}
	
	public static ReportFolderContext deleteReportFolder(ReportFolderContext reportFolderContext) throws Exception {
		
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportFolderModule().getTableName())
		.andCustomWhere("ID = ?", reportFolderContext.getId());
		
		deleteRecordBuilder.delete();
		
		return reportFolderContext;
	}
	
	public static int  deleteReportFields(Long reportId) throws Exception {
		
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportFieldsModule().getTableName())
		.andCustomWhere("REPORT_ID = ?", reportId);
		
		return deleteRecordBuilder.delete();
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
	
	public static boolean updateReport(ReportContext reportContext) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getReport1Fields())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportContext.getId(), module))
													;
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
		return updateBuilder.update(props) > 0 ? true :false ;
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
	
	public static void deleteOldReportWithWidgetUpdate(Long oldReportId,Long reportId) throws Exception {
		
		
		List<FacilioField> fields = FieldFactory.getWidgetChartFields();
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
		.table(ModuleFactory.getWidgetChartModule().getTableName())
		.fields(fields)
		.andCustomWhere(ModuleFactory.getWidgetChartModule().getTableName()+".NEW_REPORT_ID = ?",oldReportId);
		
		
		Map<String, Object> reportProp = new HashMap<>();
		reportProp.put("newReportId", reportId);
		update.update(reportProp);
		
		deleteReport(oldReportId);
	}
	
	public static void deleteReport(long reportId) throws Exception {
		
		List<WidgetChartContext> widgets = DashboardUtil.getWidgetFromDashboard(reportId,true);
		
		List<Long> removedWidgets = new ArrayList<>();
		for(WidgetChartContext widget :widgets) {
			removedWidgets.add(widget.getId());
		}
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
		.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName()+".ID", "id", StringUtils.join(removedWidgets, ","),StringOperators.IS));
		
		deleteRecordBuilder.delete();
		
		
		deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getReportModule().getTableName())
		.andCustomWhere("ID = ?", reportId);
		deleteRecordBuilder.delete();
	}
}
