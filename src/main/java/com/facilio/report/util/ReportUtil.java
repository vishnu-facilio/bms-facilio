package com.facilio.report.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.operators.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.CommonReportUtil;
import com.facilio.bmsconsole.commands.GetAllFieldsCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFactory;
import com.facilio.report.context.ReportFactory.ModuleType;
import com.facilio.report.context.ReportFactory.WorkOrder;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportFilterContext;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.context.ReportTemplateContext;
import com.facilio.report.context.ReportUserFilterContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.gson.JsonObject;

public class ReportUtil {
	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(ReportUtil.class.getName());
	
	public static ReportContext constructReport(FacilioContext context, long startTime, long endTime) throws Exception {
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		if(report == null) {
			report = new ReportContext();
		}
		report.setDateOperator(DateOperators.BETWEEN);
		report.setDateValue(startTime+", "+endTime);
		CommonReportUtil.fetchBaseLines(report, (List<ReportBaseLineContext>) context.get(FacilioConstants.ContextNames.BASE_LINE_LIST));
		
		report.setChartState((String)context.get(FacilioConstants.ContextNames.CHART_STATE));
		report.setTabularState((String)context.get(FacilioConstants.ContextNames.TABULAR_STATE));
		
		Integer dateOperator = (Integer) context.get(FacilioConstants.ContextNames.DATE_OPERATOR);
		if(dateOperator != null) {
			report.setDateOperator(dateOperator);
		}
		
		String dateVal = (String) context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE);
		if(dateVal != null) {
			report.setDateValue((String)context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE));
		}
		
		AggregateOperator xAggr = (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_X_AGGR);
		if (xAggr != null) {
			report.setxAggr(xAggr);
		}
		report.setxAlias(FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS);
		
		Boolean showAlarms = (Boolean) context.get(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS);
		if (showAlarms == null) {
			showAlarms = false;
		}
		report.addToReportState(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS, showAlarms);
		
		Boolean showSafeLimit = (Boolean) context.get(FacilioConstants.ContextNames.REPORT_SHOW_SAFE_LIMIT);
		if (showSafeLimit == null) {
			showSafeLimit = false;
		}
		report.addToReportState(FacilioConstants.ContextNames.REPORT_SHOW_SAFE_LIMIT, showSafeLimit);
		
		Integer analyticsType=(Integer) context.get(FacilioConstants.ContextNames.ANALYTICS_TYPE);
		
		if(analyticsType != null && analyticsType != -1) {
			report.setAnalyticsType(analyticsType);
		}
		
		String hmAggr=(String) context.get(FacilioConstants.ContextNames.HEATMAP_AGGR);
		
		if(hmAggr!= null) {
			report.sethmAggr(hmAggr);
		}
		
		WorkflowContext transformWorkflow = (WorkflowContext)context.get(FacilioConstants.Workflow.WORKFLOW);
		if (transformWorkflow != null) {
			report.setTransformWorkflow(transformWorkflow);
		}
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			report.setModuleId(module.getModuleId());		
		}
		
		if(context.get(FacilioConstants.ContextNames.REPORT_TEMPLATE) != null) {
			Object reportTemplate = context.get(FacilioConstants.ContextNames.REPORT_TEMPLATE);
			if(report.getTemplate() == null) {
				if(reportTemplate instanceof ReportTemplateContext) {
					report.setReportTemplate((ReportTemplateContext)reportTemplate);
				}
				else {
					String template = (String) reportTemplate;
					if(report.getTemplate() == null) {
						report.setTemplate(template);
					}
				}
			}
		}
		
		
		
		return report;
	}
	
	
	public static FacilioContext getReportData(Map<String,Object> params) throws Exception {
		
		FacilioChain fetchReadingDataChain = ReadOnlyChainFactory.newFetchReadingReportChain();
		
		FacilioContext context = fetchReadingDataChain.getContext();
		
		List<ReadingAnalysisContext> metrics = new ArrayList<>();
		if(params != null) {
			
			int xAggrInt = params.get("xAggr") != null ? Integer.parseInt(params.get("xAggr").toString()) : 0;
			
			AggregateOperator xAggr = AggregateOperator.getAggregateOperator(xAggrInt);
			
			DateOperators dateOperator = (DateOperators) Operator.getOperator(Integer.parseInt(params.get("dateOperator").toString()));
			
			ReportYAxisContext reportaxisContext = new ReportYAxisContext();
			reportaxisContext.setFieldId((Long)params.get("fieldId"));
			reportaxisContext.setAggr(Integer.parseInt(params.get("aggregateFunc").toString()));
			
			Map<String, String> aliases = new HashMap<>();
			aliases.put("actual", "A");
			
			ReadingAnalysisContext readingAnalysisContext = new ReadingAnalysisContext();
			readingAnalysisContext.setParentId(Collections.singletonList((Long)params.get("parentId")));
			readingAnalysisContext.setType(1);
			readingAnalysisContext.setAliases(aliases);
			readingAnalysisContext.setyAxis(reportaxisContext);
			
			metrics.add(readingAnalysisContext);
			
			if(params.get("startTime") != null && params.get("endTime") != null) {
				
				context.put(FacilioConstants.ContextNames.START_TIME, params.get("startTime"));
				context.put(FacilioConstants.ContextNames.END_TIME, params.get("endTime"));
			}
			else {
				
				context.put(FacilioConstants.ContextNames.START_TIME, dateOperator.getRange(null).getStartTime());
				context.put(FacilioConstants.ContextNames.END_TIME, dateOperator.getRange(null).getEndTime());
			}
			
			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, metrics);
			context.put(FacilioConstants.ContextNames.REPORT_MODE, ReportMode.TIMESERIES);
			context.put(FacilioConstants.ContextNames.REPORT_CALLING_FROM, "card");
			context.put(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN, Boolean.TRUE);
			context.put(FacilioConstants.ContextNames.ANALYTICS_TYPE, AnalyticsType.READINGS.getIntVal());
			
			fetchReadingDataChain.execute();
			
			return context;
		}
		return null;
	}
	

	public static String getAggrFieldName (FacilioField field, AggregateOperator aggr) {
		return aggr == null || aggr == CommonAggregateOperator.ACTUAL ? field.getName() : field.getName()+"_"+aggr.getStringValue();
	}
	
	public static List<ReportFolderContext> getAllReportFolder(String moduleName,boolean isWithReports, String searchText) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioModule reportFoldermodule = ModuleFactory.getReportFolderModule();
		List<FacilioField> fields = FieldFactory.getReport1FolderFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(fields)
													.table(reportFoldermodule.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportFoldermodule))
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
													;
		
		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}
		
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
		SharingContext<SingleSharingContext> sharing = SharingAPI.getSharingList(ModuleFactory.getReportSharingModule(), SingleSharingContext.class);
		Map<Long, SharingContext<SingleSharingContext>> map = new HashMap<>();
		for (int j = 0; j < sharing.size(); j++) {
			if (map.containsKey(sharing.get(j).getParentId())){
				map.get(sharing.get(j).getParentId()).add(sharing.get(j));
			}
			else {
				SharingContext<SingleSharingContext> temp = new SharingContext<SingleSharingContext>();
				temp.add(sharing.get(j));
				map.put(sharing.get(j).getParentId(), temp);
			}
		}
		for (int i = 0; i < reportFolders.size(); i++) {
			
			if (map.containsKey(reportFolders.get(i).getId())) {
				
				reportFolders.get(i).setReportSharing(map.get(reportFolders.get(i).getId()));;	
			}
		}
		return getFilteredReport(reportFolders);
	}
	@SuppressWarnings("unlikely-arg-type")
	public static List<ReportFolderContext> getFilteredReport(List<ReportFolderContext> reportFolders) throws Exception {
		List<ReportFolderContext> reportFolder = new ArrayList<ReportFolderContext>();
		if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole().getName().equals(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN)) {
			return reportFolders;
		}
		for(ReportFolderContext pro : reportFolders) 
		{

			if (pro.getReportSharing().isEmpty() || pro.getReportSharing() == null) {
				reportFolder.add(pro);
			}
			else if (pro.getReportSharing().isAllowed(pro)) {
				reportFolder.add(pro);
			}
		}
		
		return reportFolder;

		
	}
	public static void moveReport(ReportContext reportContext) throws Exception {
		FacilioModule module = ModuleFactory.getReportModule();
		List< FacilioField> updateFields = FieldFactory.getReport1Fields();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName()).andCustomWhere("ID = ?", reportContext.getId()).fields(updateFields);
		
		Map<String,Object> prop = FieldUtil.getAsProperties(reportContext);
		updateBuilder.update(prop);
		
	}
	public static List<ReportContext> getReportsFromFolderId(long folderId) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCustomWhere(module.getTableName()+".REPORT_FOLDER_ID = ?",folderId)
													;
		
		List<Map<String, Object>> props = select.get();
		List<ReportContext> reports = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			int i = 0;
			for(Map<String, Object> prop :props) {
				try {
					ReportContext report = getReportContextFromProps(prop);
					reports.add(report);
				}
				catch (Exception e) {
					LOGGER.info("Error in report conversion, folderId:" + folderId +", index: " + i, e);
					throw e;
				}
				i++;
			}
		}
		return reports;
	}
	
	public static List<ReportContext> getReports(String moduleName, String searchText) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioModule reportModule = ModuleFactory.getReportModule();
		List<FacilioField> fields = FieldFactory.getReport1Fields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(fields)
													.table(reportModule.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportModule))
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		
		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}
		
		List<Map<String, Object>> props = select.get();
		List<ReportContext> reports = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				
				ReportContext report = getReportContextFromProps(prop);
				reports.add(report);
			}
		}
		return reports;
	}
	
	public static ReportContext getReportContextFromProps(Map<String, Object> prop) {
		ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
		DateRange actualRange = null;
		if(report.getDateRange() == null && report.getDateOperatorEnum() != null) {
			actualRange = report.getDateOperatorEnum().getRange(report.getDateValue());
			report.setDateRange(actualRange);
		}
		return report;
	}
	
	public static ReportContext getReport(long reportId, Boolean...fetchReportOnly) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportId, module))
													;
		
		List<ReportContext> reports = getReportsFromProps(select.get(), fetchReportOnly);
		if (reports != null && !reports.isEmpty()) {
			return reports.get(0);
		}
		return null;
	}
	
	public static List<ReportContext> fetchAllReportsByType(Integer reportType) throws Exception{
		List<ReportContext> reportList = new ArrayList<ReportContext>();
		
		
		FacilioModule module = ModuleFactory.getReportModule();
		
		List<FacilioField> allFields = FieldFactory.getReport1Fields();
		FacilioField typeField = new FacilioField();
		for(FacilioField field: allFields) {
			if(field.getName().equals("type")) {
				typeField = field;
				break;
			}
		}
		
		
		Condition newTypeCondition = CriteriaAPI.getCondition(typeField.getColumnName(), typeField.getName(), reportType.toString(), StringOperators.IS);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
													.andCondition(newTypeCondition);

		reportList = getReportsFromProps(selectBuilder.get(), true);
		return reportList;
	}
	
	private static List<ReportContext> getReportsFromProps(List<Map<String, Object>> props, Boolean...fetchReportOnly) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<ReportContext> reports = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			boolean fetchReportContextOnly = fetchReportOnly != null && fetchReportOnly.length > 0 && fetchReportOnly[0];
			for (Map<String, Object> prop : props) {
				ReportContext report = FieldUtil.getAsBeanFromMap(prop, ReportContext.class);
				if (fetchReportContextOnly) {
					reports.add(report);
					continue;
				}
				if (report.getFilters() != null && !report.getFilters().isEmpty()) {
					for (ReportFilterContext filter : report.getFilters()) {
						filter.setField(getModule(filter.getModuleId(), filter.getModuleName(), modBean), getField(filter.getFieldId(), filter.getModuleName(), filter.getFieldName(), modBean));
					}
				}
				if (report.getWorkflowId() != -1) {
					report.setTransformWorkflow(WorkflowUtil.getWorkflowContext(report.getWorkflowId(), true));
				}
				
				for (ReportDataPointContext dataPoint : report.getDataPoints()) {
					ReportFieldContext xAxis = dataPoint.getxAxis();
					xAxis.setField(getModule(xAxis.getModuleId(), xAxis.getModuleName(), modBean), getField(xAxis.getFieldId(), xAxis.getModuleName(), xAxis.getFieldName(), modBean));
					
					ReportYAxisContext yAxis = dataPoint.getyAxis();
					yAxis.setField(getModule(yAxis.getModuleId(), yAxis.getModuleName(), modBean), getField(yAxis.getFieldId(), yAxis.getModuleName(), yAxis.getFieldName(), modBean));
					
					if (CollectionUtils.isNotEmpty(dataPoint.getGroupByFields())) {
						for (ReportGroupByField groupByField : dataPoint.getGroupByFields()) {
							groupByField.setField(getModule(groupByField.getModuleId(), groupByField.getModuleName(), modBean), getField(groupByField.getFieldId(), groupByField.getModuleName(), groupByField.getFieldName(), modBean));
						}
					}
					
					if (CollectionUtils.isNotEmpty(report.getUserFilters())) {
						for (ReportUserFilterContext reportUserFilterContext : report.getUserFilters()) {
							reportUserFilterContext.setField(modBean.getField(reportUserFilterContext.getFieldId()));
						}
					}

					ReportFieldContext dateReportField = dataPoint.getDateField();
					if (dataPoint.getDateFieldId() > 0 || dataPoint.getDateFieldName() != null) {
						if (dateReportField == null) {
							dateReportField = new ReportFieldContext();
						}
						dateReportField.setField(null, getField(dataPoint.getDateFieldId(), dataPoint.getDateFieldModuleName(), dataPoint.getDateFieldName(), modBean));
					}
					dataPoint.setDateField(dateReportField);
				}
				reports.add(report);
			}
			return reports;
		}
		return null;
	}

	private static FacilioModule getModule(long moduleId, String moduleName, ModuleBean modBean) throws Exception {
		if (moduleId != -1) {
			return modBean.getModule(moduleId);
		}
		else if (StringUtils.isNotEmpty(moduleName)) {
			return modBean.getModule(moduleName);
		}
		return null;
	}
	
	private static FacilioField getField(long fieldId, String moduleName, String fieldName, ModuleBean modBean) throws Exception {
		if (fieldId != -1) {
			return modBean.getField(fieldId);
		}
		FacilioField field = null;
		if (moduleName != null && !moduleName.isEmpty() && fieldName != null && !fieldName.isEmpty()) {
			field = modBean.getField(fieldName, moduleName);
			if (field != null) {
				return field;
			}
		}
		if (field == null && StringUtils.isNotEmpty(fieldName)) {
			field = ReportFactory.getReportField(fieldName);
		}
		return field;
	}
	
	public static List<Map<String, Object>> getReportFromFolderId(long reportFolderId) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(FieldFactory.getReport1Fields())
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCustomWhere("REPORT_FOLDER_ID = ?", reportFolderId);
													;
		
		List<Map<String, Object>> props = select.get();
		return props;
	}
	
	public static ReportFolderContext addReportFolder(ReportFolderContext reportFolderContext) throws Exception {
		FacilioContext context = new FacilioContext();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.fields(FieldFactory.getReport1FolderFields());

		reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		reportFolderContext.setModifiedTime(System.currentTimeMillis());
		Map<String, Object> props = FieldUtil.getAsProperties(reportFolderContext);
		long id = insertBuilder.insert(props);
		reportFolderContext.setId(id);
		if (!reportFolderContext.getReportSharing().isEmpty() && reportFolderContext.getReportSharing() != null) {
//		SharingAPI.deleteSharing(reportFolderContext.getReportSharing().stream().map(SingleSharingContext::getId).collect(Collectors.toList()), ModuleFactory.getReportSharingModule());
		SharingAPI.addSharing(reportFolderContext.getReportSharing(), id, ModuleFactory.getReportSharingModule());
		reportFolderContext.setReportSharing(SharingAPI.getSharing(id, ModuleFactory.getReportSharingModule(), SingleSharingContext.class));
		}
		return reportFolderContext;
	}
	
	public static ReportFolderContext updateReportFolder(ReportFolderContext reportFolderContext) throws Exception {
		
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.fields(FieldFactory.getReport1FolderFields())
				.andCustomWhere("ID = ?", reportFolderContext.getId());
		
		reportFolderContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		reportFolderContext.setModifiedTime(System.currentTimeMillis());
		
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
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportContext.getId(), module))
													;
		
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
		return updateBuilder.update(props) > 0 ? true :false ;
	}
	
	public static void addReportFields(Collection<ReportFieldContext> reportFieldContexts) throws Exception {
		
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

	public static JSONObject getReportFields(String moduleName) throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, -1l);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new GetAllFieldsCommand());
		c.execute(context);
		
		FacilioModule resourceModule = ModuleFactory.getResourceModule();
		
		JSONObject jsonObject = (JSONObject) context.get("meta");
		Map<String, List<FacilioField>> dimensionFieldMap = new HashMap<>();
		List<FacilioField> metricFields = new ArrayList<>();
		List<FacilioField> list = (List<FacilioField>) jsonObject.get("fields");
		for (FacilioField field : list) {
			if (field instanceof NumberField) {
				metricFields.add(field);
			} else if (field instanceof LookupField) {
				if (((LookupField) field).getLookupModule() != null && ((LookupField) field).getLookupModule().equals(resourceModule)) {
					addFieldInList(dimensionFieldMap, "resource_fields", field);
				} else {
					addFieldInList(dimensionFieldMap, moduleName, field);
				}
			} else if (field.getDataTypeEnum() == FieldType.DATE || field.getDataTypeEnum() == FieldType.DATE_TIME) {
				addFieldInList(dimensionFieldMap, "time", field);
			}
		}
		
		List<ModuleType> moduleTypes = new ArrayList<ModuleType>();

		if (moduleName.equals("workorder")) {
			metricFields.add(ReportFactory.getReportField(WorkOrder.FIRST_RESPONSE_TIME_COL));
			
			List<FacilioField> workorderFields = dimensionFieldMap.get(moduleName);
			workorderFields.add(ReportFactory.getReportField(WorkOrder.OPENVSCLOSE_COL));
			workorderFields.add(ReportFactory.getReportField(WorkOrder.OVERDUE_OPEN_COL));
			workorderFields.add(ReportFactory.getReportField(WorkOrder.OVERDUE_CLOSED_COL));
			workorderFields.add(ReportFactory.getReportField(WorkOrder.PLANNED_VS_UNPLANNED_COL));

			moduleTypes.add(new ModuleType("Workorders", 1));
			moduleTypes.add(new ModuleType("Workrequests", 2));
		}
		
		jsonObject.put("dimension", dimensionFieldMap);
		jsonObject.put("metrics", metricFields);
		jsonObject.put("moduleType", moduleTypes);

		System.out.println(context);
		return jsonObject;
	}
	
	private static void addFieldInList(Map<String, List<FacilioField>> map, String name, FacilioField field) {
		List<FacilioField> list = map.get(name);
		if (list == null) {
			list = new ArrayList<>();
			map.put(name, list);
		}
		list.add(field);
	}
	
	@SuppressWarnings("unchecked")
	public static void setAliasForDataPoints(JSONArray dataPoints, long baselineId) throws Exception {
		char lastAlias = 'A';
		BaseLineContext bl = null;
		if (baselineId > -1) {
			bl = BaseLineAPI.getBaseLine(baselineId);
		}
		for(int i = 0; i < dataPoints.size(); i++) {
			JSONObject pointObj = (JSONObject) dataPoints.get(i);
			JSONObject aliasesObj = new JSONObject();
			if (lastAlias == 'E' || lastAlias == 'X') {
				++lastAlias;
			}
			aliasesObj.put(FacilioConstants.Reports.ACTUAL_DATA, String.valueOf(lastAlias++));
			pointObj.put("aliases", aliasesObj);
			if (bl != null) {
				if (lastAlias == 'E' || lastAlias == 'X') {
					++lastAlias;
				}
				aliasesObj.put(bl.getName(), String.valueOf(lastAlias++));
			}
		}
	}
	
	public static List<ReportFolderContext> getAllCustomModuleReportFolder(boolean isWithReports, String searchText) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> moduleIds = new ArrayList<Long>();
		
		FacilioModule reportFoldermodule = ModuleFactory.getReportFolderModule();
		FacilioModule modulesmodule = ModuleFactory.getModuleModule();
		List<FacilioField> fields = FieldFactory.getReport1FolderFields();
		List<FacilioField> moduleFields = FieldFactory.getModuleFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Map<String,FacilioField> moduleFieldMap = FieldFactory.getAsMap(moduleFields);
		
		GenericSelectRecordBuilder selectModules = new GenericSelectRecordBuilder()
															.select(Collections.singletonList(moduleFieldMap.get("moduleId")))
															.table(modulesmodule.getTableName())
															.andCondition(CriteriaAPI.getCondition(moduleFieldMap.get("type"),String.valueOf(FacilioModule.ModuleType.BASE_ENTITY.getValue()), NumberOperators.EQUALS))
															.andCondition(CriteriaAPI.getCondition(moduleFieldMap.get("custom"),String.valueOf(true), BooleanOperators.IS));
		
		List<Map<String, Object>> moduleprops = selectModules.get();
		
		LOGGER.info("MODULEBUILDER" + selectModules);
		LOGGER.info("MODULEPROPS" + moduleprops);
		
		if(moduleprops != null && !moduleprops.isEmpty()) {
			for(Map<String, Object> moduleprop :moduleprops) {
				moduleIds.add((Long) moduleprop.get("moduleId"));
			}
			
		}
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(fields)
													.table(reportFoldermodule.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportFoldermodule))
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), moduleIds,NumberOperators.EQUALS));
													;
		
		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}
		
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
		SharingContext<SingleSharingContext> sharing = SharingAPI.getSharingList(ModuleFactory.getReportSharingModule(), SingleSharingContext.class);
		Map<Long, SharingContext<SingleSharingContext>> map = new HashMap<>();
		for (int j = 0; j < sharing.size(); j++) {
			if (map.containsKey(sharing.get(j).getParentId())){
				map.get(sharing.get(j).getParentId()).add(sharing.get(j));
			}
			else {
				SharingContext<SingleSharingContext> temp = new SharingContext<SingleSharingContext>();
				temp.add(sharing.get(j));
				map.put(sharing.get(j).getParentId(), temp);
			}
		}
		for (int i = 0; i < reportFolders.size(); i++) {
			
			if (map.containsKey(reportFolders.get(i).getId())) {
				
				reportFolders.get(i).setReportSharing(map.get(reportFolders.get(i).getId()));;	
			}
		}
		return getFilteredReport(reportFolders);
	}
	public static String getAlias(String previous){
		String alias = "A";
		if(StringUtils.isAlpha(previous)){
			int preIndex = previous.length()-1;
			char preChar = previous.charAt(preIndex);
			if(preChar == 'Z'){
				previous = previous.substring(0, preIndex);
				if(StringUtils.isNotEmpty(previous)){
					alias = getAlias(previous)+alias;
				}else{
            	    alias+=alias;				    
				}
			}else{
				alias = previous.substring(0, preIndex)+Character.toString(++preChar);
			}
		}
		return alias;
	}
}
