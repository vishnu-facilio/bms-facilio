package com.facilio.report.util;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Group;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.report.V3ScheduledReportRelContext;
import com.facilio.bmsconsoleV3.util.HashMapValueComparator;
import com.facilio.db.criteria.Criteria;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.db.criteria.operators.*;
import com.facilio.fs.FileInfo;
import com.facilio.modules.*;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.context.ReportUserFilterContext;
import com.facilio.services.filestore.PublicFileUtil;
import com.facilio.services.pdf.PDFOptions;
import com.facilio.services.pdf.PDFService;
import com.facilio.services.pdf.PDFServiceFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.CommonReportUtil;
import com.facilio.bmsconsole.commands.GetAllFieldsCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
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
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.SpaceAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.context.ReportFactory.ModuleType;
import com.facilio.report.context.ReportFactory.WorkOrder;
import com.facilio.report.context.ReportFolderContext.FolderType;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

import javax.servlet.http.HttpServletRequest;

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
		
		String scatterConfig = (String) context.get(FacilioConstants.ContextNames.REPORT_SCATTER_CONFIG);
		
		if(StringUtils.isNotEmpty(scatterConfig)) {
			report.addToReportState(FacilioConstants.ContextNames.REPORT_SCATTER_CONFIG, scatterConfig);
		}
		
		AggregateOperator groupByTimeAggr= (AggregateOperator) context.get(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR);
		
		if(groupByTimeAggr!= null) {
			report.setgroupByTimeAggr(groupByTimeAggr);
			report.addToReportState(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, groupByTimeAggr.getValue());
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
		String timeFilter = (String) context.get(FacilioConstants.ContextNames.TIME_FILTER);
		if(StringUtils.isNotEmpty(timeFilter)) {
			report.setTimeFilter(timeFilter);
		}
		String dataFilter = (String) context.get(FacilioConstants.ContextNames.DATA_FILTER);
		if(StringUtils.isNotEmpty(dataFilter)) {
			report.setDataFilter(dataFilter);
		}
		
		
		
		return report;
	}

public static FacilioContext Constructpivot(FacilioContext context,long jobId) throws Exception {
	FacilioModule module = ModuleFactory.getReportScheduleInfo();
	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
			.table(module.getTableName())
			.select(FieldFactory.getReportScheduleInfo1Fields())
			.andCondition(CriteriaAPI.getIdCondition(jobId, module));
	List<Map<String, Object>> props = builder.get();
	Map<String, Object> prop = null;
	if (props != null && !props.isEmpty() && props.get(0) != null) {
		prop = props.get(0);
	}
	Long reportId = (long) prop.get("reportId");
	ReportContext reportContext = ReportUtil.getReport(reportId);

	JSONParser parser = new JSONParser();
	ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson(
			(JSONObject) parser.parse(reportContext.getTabularState()), ReportPivotParamsContext.class);
	context.put(FacilioConstants.Reports.ROWS, pivotparams.getRows());
	context.put(FacilioConstants.Reports.DATA, pivotparams.getData());
	context.put(FacilioConstants.ContextNames.VALUES, pivotparams.getValues());
	context.put(FacilioConstants.ContextNames.FORMULA, pivotparams.getFormula());
	context.put(FacilioConstants.ContextNames.MODULE_NAME, pivotparams.getModuleName());
	context.put(FacilioConstants.ContextNames.CRITERIA, pivotparams.getCriteria());
	context.put(FacilioConstants.ContextNames.SORTING, pivotparams.getSortBy());
	context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, pivotparams.getTemplateJSON());
	context.put(FacilioConstants.ContextNames.DATE_FIELD, pivotparams.getDateFieldId());
	context.put(FacilioConstants.ContextNames.DATE_OPERATOR, pivotparams.getDateOperator());
	context.put(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, pivotparams.getShowTimelineFilter());
	context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, pivotparams.getDateValue());
	context.put(FacilioConstants.ContextNames.IS_EXPORT_REPORT, true);
	context.put(FacilioConstants.ContextNames.IS_BUILDER_V2, pivotparams.isBuilderV2());
	context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, false);
	context.put(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
	context.put(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
	return null;
}


	public static FacilioContext getReportData(Map<String,Object> params) throws Exception {
		
		FacilioChain fetchReadingDataChain = ReadOnlyChainFactory.newFetchReadingReportChain();

		FacilioContext context = fetchReadingDataChain.getContext();

		List<ReadingAnalysisContext> metrics = new ArrayList<>();
		if(params != null) {


			int xAggrInt = params.get("xAggr") != null ? Integer.parseInt(params.get("xAggr").toString()) : 0;

			AggregateOperator xAggr = AggregateOperator.getAggregateOperator(xAggrInt);

			DateOperators dateOperator = params.get("dateOperator") != null?(DateOperators) Operator.getOperator(Integer.parseInt(params.get("dateOperator").toString())):null;

			if(params.get("fields") != null) {
				JSONParser parser = new JSONParser();
				JSONArray fieldArray = (JSONArray) parser.parse(params.get("fields").toString());
				metrics = FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class);
			} else {
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
			}

			if(params.get("startTime") != null && params.get("endTime") != null) {

				context.put(FacilioConstants.ContextNames.START_TIME, params.get("startTime"));
				context.put(FacilioConstants.ContextNames.END_TIME, params.get("endTime"));
			}
			else if(dateOperator != null){

				context.put(FacilioConstants.ContextNames.START_TIME, dateOperator.getRange(null).getStartTime());
				context.put(FacilioConstants.ContextNames.END_TIME, dateOperator.getRange(null).getEndTime());
			}

			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, metrics);
			if(params.get("mode") != null) {
				context.put(FacilioConstants.ContextNames.REPORT_MODE, ReportMode.valueOf(Integer.parseInt(params.get("mode").toString())));
			} else {
				context.put(FacilioConstants.ContextNames.REPORT_MODE, ReportMode.TIMESERIES);
			}
			context.put(FacilioConstants.ContextNames.REPORT_CALLING_FROM, "card");
			context.put(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN, Boolean.TRUE);
			if(params.get("analyticsType") != null) {
				context.put(FacilioConstants.ContextNames.ANALYTICS_TYPE, Integer.parseInt(params.get("analyticsType").toString()));
			} else {
				context.put(FacilioConstants.ContextNames.ANALYTICS_TYPE, AnalyticsType.READINGS.getIntVal());
			}
			fetchReadingDataChain.execute();

			return context;
		}
		return null;
	}
	

	public static String getAggrFieldName (FacilioField field, AggregateOperator aggr) {
		return aggr == null || aggr == CommonAggregateOperator.ACTUAL ? field.getName() : field.getName()+"_"+aggr.getStringValue();
	}
	
	public static List<ReportFolderContext> getAllReportFolder(String moduleName,boolean isWithReports, String searchText, Boolean isPivot, Long webTabId) throws Exception {
		ApplicationContext app = ApplicationApi.getApplicationForId(AccountUtil.getCurrentUser().getApplicationId());
		boolean isMainApp = (app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
		long appId = (long) AccountUtil.getCurrentUser().getApplicationId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if(isMainApp) {
			FacilioModule module = modBean.getModule(moduleName);
			reportFolders.addAll(ReportUtil.getReportFolders(module.getModuleId(), searchText, isMainApp, isPivot));
			if(moduleName != null && (moduleName.equals("newreadingalarm") || moduleName.equals("bmsalarm"))){
				FacilioModule newmodule = modBean.getModule("alarm");
				reportFolders.addAll(ReportUtil.getReportFolders(newmodule.getModuleId(), searchText, isMainApp, isPivot));
			}
		}
		else
		{
			Boolean isAnalyticsReport = false;
			List<Long> moduleIds = new ArrayList<>();
			if(webTabId != null && webTabId > 0)
			{
				WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
				WebTabContext webTabContext = tabBean.getWebTab(webTabId);
				JSONObject config = webTabContext.getConfigJSON();
				if(config != null && config.containsKey("type") && config.get("type").equals("module_reports"))
				{
					List<Long> webtab_moduleIds = ApplicationApi.getModuleIdsForTab(webTabContext.getId());
					if(webtab_moduleIds != null && webtab_moduleIds.size() > 0)
					{
						for(Long moduleId : webtab_moduleIds)
						{
							FacilioModule module = modBean.getModule(moduleId);
							if(module != null && !module.isCustom())
							{
								Set<FacilioModule> subModules = ReportFactoryFields.getSubModulesList(module.getName());
								if(subModules != null){
									moduleIds.addAll(subModules.stream().map(m -> m.getModuleId()).collect(Collectors.toList()));
								}
							}
							if(module != null) {
								moduleIds.add(moduleId);
							}
						}
					}
				}
				else if((config != null && config.containsKey("type") && config.get("type").equals("analytics_reports")) || isPivot){
					FacilioModule energyData = modBean.getModule("energydata");
					moduleIds.add(energyData.getModuleId());
					isAnalyticsReport = true;
				}
			}
			else if(isPivot)
			{
				FacilioModule energyData = modBean.getModule("energydata");
				moduleIds.add(energyData.getModuleId());
			}
			else if( appId != 0 )
			{
				if(moduleName != null && (moduleName.equals("energydata") || moduleName.equals("energyData"))){
					FacilioModule energyData = modBean.getModule("energydata");
					moduleIds.add(energyData.getModuleId());
					isAnalyticsReport = true;
				}
				else
				{
					FacilioModule module = modBean.getModule(moduleName);
					moduleIds.add(module.getModuleId());
					Set<FacilioModule> subModulesList = ReportFactoryFields.getSubModulesList(moduleName);
					if(subModulesList != null){
						moduleIds.addAll(subModulesList.stream().map(m -> m.getModuleId()).collect(Collectors.toList()));
					}
				}
			}
			if(moduleIds != null && !moduleIds.isEmpty())
			{
				FacilioChain chain = ReadOnlyChainFactory.geAllModulesChain();
				FacilioContext context = chain.getContext();
				chain.execute();
				List<JSONObject> modules = (ArrayList<JSONObject>)context.get("systemModules");
				List<JSONObject> customModules = (ArrayList<JSONObject>)context.get("customModules");
				modules.addAll(customModules);

				List<Long> moduleIds_list = new ArrayList<>();
				for(JSONObject module_obj : modules){
					moduleIds_list.add((Long)module_obj.get("moduleId"));
				}
				if(isPivot || isAnalyticsReport)
				{
					FacilioModule energyData = modBean.getModule("energydata");
					moduleIds_list.add(energyData.getModuleId());
				}
				if(moduleName != null && moduleName.equals("alarm")){
					FacilioModule alarm_module = modBean.getModule(moduleName);
					if(alarm_module != null){
						moduleIds.add(alarm_module.getModuleId());
						moduleIds_list.add(alarm_module.getModuleId());
						FacilioModule new_alarm_module = modBean.getModule("newreadingalarm");
						if(new_alarm_module != null && !moduleIds.contains(new_alarm_module.getModuleId()))
						{
							moduleIds.add(new_alarm_module.getModuleId());
						}
					}
				}
				List<Long> newList = moduleIds.stream().distinct().collect(Collectors.toList());
				for(long moduleId : newList)
				{
					if(moduleIds_list != null && moduleIds_list.contains(moduleId)) {
						reportFolders.addAll(ReportUtil.getReportFolders(moduleId, searchText, isMainApp, isPivot));
					}
				}
			}
		}
		return getFilteredReport(reportFolders);
	}

	public static List<ReportFolderContext> getReportFolders(Long moduleId, String searchText, Boolean isMainApp, Boolean isPivot)throws Exception
	{
		long appId = (long) AccountUtil.getCurrentUser().getApplicationId();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule othermodule = modBean.getModule(moduleId);
		List<FacilioField> otherfields = FieldFactory.getReport1FolderFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(otherfields);

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(otherfields)
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(othermodule.getModuleId()), NumberOperators.EQUALS));

		if(isPivot)
		{
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("folderType"), String.valueOf(FolderType.PIVOT.getValue()), PickListOperators.IS));
			if(isMainApp){
				select.andCriteria(ReportUtil.getReportAppIdCriteria(fieldMap, appId));
			} else{
				select.andCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(appId), NumberOperators.EQUALS));
			}
		}
		else
		{
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("folderType"), String.valueOf(FolderType.PIVOT.getValue()), PickListOperators.ISN_T));
			Criteria addAppIdCriteria = ReportUtil.getReportAppIdCriteria(fieldMap, appId);
			select.andCriteria(addAppIdCriteria);
		}
		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}

		FacilioModule reportModule = ModuleFactory.getReportModule();
		List<FacilioField> otherreportSelectFields = new ArrayList<>();

		otherreportSelectFields.add(FieldFactory.getIdField(reportModule));
		otherreportSelectFields.add(FieldFactory.getSiteIdField(reportModule));
		otherreportSelectFields.add(FieldFactory.getField("reportFolderId", "REPORT_FOLDER_ID", reportModule, FieldType.LOOKUP));
		otherreportSelectFields.add(FieldFactory.getField("name", "NAME", reportModule, FieldType.STRING));
		otherreportSelectFields.add(FieldFactory.getField("description", "DESCRIPTION", reportModule, FieldType.STRING));
		otherreportSelectFields.add(FieldFactory.getField("type", "REPORT_TYPE", reportModule, FieldType.NUMBER));
		otherreportSelectFields.add(FieldFactory.getField("analyticsType", "ANALYTICS_TYPE", reportModule, FieldType.NUMBER));
		otherreportSelectFields.add(FieldFactory.getModuleIdField(reportModule));
		otherreportSelectFields.add(FieldFactory.getField("createdTime", "CREATED_TIME", reportModule, FieldType.NUMBER));
		otherreportSelectFields.add(FieldFactory.getField("modifiedTime", "MODIFIED_TIME", reportModule, FieldType.NUMBER));
		otherreportSelectFields.add(FieldFactory.getField("createdBy", "CREATED_BY", reportModule, FieldType.NUMBER));
		otherreportSelectFields.add(FieldFactory.getField("modifiedBy", "MODIFIED_BY", reportModule, FieldType.NUMBER));
		otherreportSelectFields.add(FieldFactory.getField("appId", "APP_ID", reportModule, FieldType.NUMBER));

		List<Map<String, Object>> otherprops = select.get();
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if (otherprops != null && !otherprops.isEmpty()) {

			for (Map<String, Object> prop : otherprops) {
				ReportFolderContext folder = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
				List<ReportContext> reports = getReportsFromFolderId(folder.getId(), otherreportSelectFields, isMainApp, isPivot);
				folder.setReports(reports);
				reportFolders.add(folder);
			}
		}
		Map<Long, SharingContext<SingleSharingContext>> othermap = SharingAPI.getSharingMap(ModuleFactory.getReportSharingModule(), SingleSharingContext.class);
		for (int i = 0; i < reportFolders.size(); i++) {

			if (othermap.containsKey(reportFolders.get(i).getId())) {
				reportFolders.get(i).setReportSharing(othermap.get(reportFolders.get(i).getId()));
			}
		}
		return reportFolders;
	}
	public static Criteria getReportAppIdCriteria(Map<String, FacilioField> fieldMap, long appId)throws Exception
	{
		Criteria appCriteria = new Criteria();
		appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(appId), NumberOperators.EQUALS));
		appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), "NULL", CommonOperators.IS_EMPTY));
		return appCriteria;
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
	public static List<ReportContext> getReportsFromFolderIdNew(long folderId) throws Exception {

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
	public static List<ReportContext> getReportsFromFolderId(long folderId, List<FacilioField> selectFields) throws Exception {
		return getReportsFromFolderId(folderId, selectFields, true, false);
	}
	public static List<Long> getLoggedInUserGroupIds () {
		List<Long> objs = new ArrayList<Long>();
		try {
			List<Group> myGroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
			if (myGroups != null && !myGroups.isEmpty()) {
				objs = myGroups.stream().map(Group::getId).collect(Collectors.toList());
			}
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
		return objs;
	}

	public static void getReportSharingPermission(List<ReportContext> reports, ReportContext report) throws Exception {
		Long parentId = report.getId();
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getReportShareModule().getTableName())
				.select(FieldFactory.getReportShareField())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = select.get();
		long currentUserId = AccountUtil.getCurrentUser().getId();
		long currentUserRoleId = AccountUtil.getCurrentUser().getRoleId();
		List<Long> currentUserGroupId = getLoggedInUserGroupIds();
		if(props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				if (prop.containsKey("userId")) {
					if (currentUserId == (Long) prop.get("userId")) {
						reports.add(report);
						break;
					}
				}
				else if (prop.containsKey("roleId")) {
					if (currentUserRoleId == (Long) prop.get("roleId")) {
						reports.add(report);
						break;
					}
				}
				else if (prop.containsKey("groupId")) {
					if(currentUserGroupId.contains((Long) prop.get("groupId"))){
                         reports.add(report);
						 break;
					}
				}
			}
		}
		else{
			reports.add(report);
		}
	}


	public static List<ReportContext> getReportsFromFolderId(long folderId, List<FacilioField> selectFields, boolean isMainApp, boolean isPivot) throws Exception {
		FacilioModule module = ModuleFactory.getReportModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(selectFields)
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCustomWhere(module.getTableName()+".REPORT_FOLDER_ID = ?",folderId)
													;
		if(isMainApp)
		{
			select.andCriteria(ReportUtil.getReportAppIdCriteria(fieldMap, AccountUtil.getCurrentUser().getApplicationId()));
		}
		else if(!isMainApp && isPivot){
			select.andCondition(CriteriaAPI.getCondition(module.getTableName()+".APP_ID", "appId", AccountUtil.getCurrentUser().getApplicationId()+"", NumberOperators.EQUALS));
		}
		if(!isMainApp && !isPivot){
			Criteria appIdCriteria = ReportUtil.getReportAppIdCriteria(fieldMap, AccountUtil.getCurrentUser().getApplicationId());
			select.andCriteria(appIdCriteria);
		}
		List<Map<String, Object>> props = select.get();
		List<ReportContext> reports = new ArrayList<>();
		if(props != null && !props.isEmpty()) {
			int i = 0;
			for(Map<String, Object> prop :props) {
				try {
					ReportContext report = getReportContextFromProps(prop);
					if(report !=null && report.getModuleId() > 0){
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						module = modBean.getModule(report.getModuleId());
						report.setModuleName(module.getDisplayName());
					}
					Boolean isUserPrevileged = AccountUtil.getCurrentUser().getRole().getIsPrevileged();
					if(isUserPrevileged==null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.REPORT_SHARE)){
						getReportSharingPermission(reports, report);
					}
					else{
						reports.add(report);
					}
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
	public static List<ReportContext> getReportsFromFolderIdNew(long folderId, List<FacilioField> selectFields, boolean isMainApp, Context context) throws Exception {

		boolean isWithCount = (boolean) context.get(Constants.WITH_COUNT);
				FacilioModule module = ModuleFactory.getReportModule();
		List<ReportContext> reports = new ArrayList<>();
		GenericSelectRecordBuilder selectBuilder = getSelectRecordsBuilder(isMainApp, selectFields, context);
		if(isWithCount){
			GenericSelectRecordBuilder countSelect = getSelectRecordsBuilder(isMainApp, selectFields, context);
			countSelect.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
			List<Map<String, Object>> countRecord = countSelect.get();
			context.put(Constants.COUNT, countRecord.get(0).get("id"));
		}
		else {
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				int i = 0;
				for (Map<String, Object> prop : props) {
					try {
						ReportContext report = getReportContextFromProps(prop);
						if (report != null && report.getModuleId() > 0) {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							module = modBean.getModule(report.getModuleId());
							report.setModuleName(module.getDisplayName());
						}
						Boolean isUserPrevileged = AccountUtil.getCurrentUser().getRole().getIsPrevileged();
						if (isUserPrevileged == null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.REPORT_SHARE)) {
							getReportSharingPermission(reports, report);
						} else {
							reports.add(report);
						}
					} catch (Exception e) {
						LOGGER.info("Error in report conversion, folderId:" + folderId + ", index: " + i, e);
						throw e;
					}
					i++;
				}
			}
		}
			return reports;

	}
	private static GenericSelectRecordBuilder getSelectRecordsBuilder(boolean isMainApp, List<FacilioField> selectFields, Context context) throws Exception{

		boolean isWithCount = (boolean) context.get(Constants.WITH_COUNT);
		boolean isPivot = (boolean) context.get("isPivot");
		long folderId = (long) context.get("folderId");
		String searchText = (String) context.get("searchText");
		FacilioModule module = ModuleFactory.getReportModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()

				.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere(module.getTableName()+".REPORT_FOLDER_ID = ?",folderId)
				;
		if(!isWithCount){
			select.select(selectFields);
		}
		else{
			select.select(new HashSet<>());
		}
		if(isMainApp)
		{
			select.andCriteria(ReportUtil.getReportAppIdCriteria(fieldMap, AccountUtil.getCurrentUser().getApplicationId()));
		}
		else if(!isMainApp && isPivot){
			select.andCondition(CriteriaAPI.getCondition(module.getTableName()+".APP_ID", "appId", AccountUtil.getCurrentUser().getApplicationId()+"", NumberOperators.EQUALS));
		}
		if(!isMainApp && !isPivot){
			Criteria appIdCriteria = ReportUtil.getReportAppIdCriteria(fieldMap, AccountUtil.getCurrentUser().getApplicationId());
			select.andCriteria(appIdCriteria);
		}
		if (searchText != null && !searchText.isEmpty()) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
			select.andCriteria(criteria);
		}
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);

		int perPage;
		int page;
		if (pagination != null) {
			page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
			perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
		} else {
			page = 1;
			perPage = 50;
		}
		int offset = ((page-1) * perPage);
		if (offset < 0) {
			offset = 0;
		}

		select.offset(offset);
		select.limit(perPage);
		String orderBy = FieldFactory.getIdField(module).getCompleteColumnName()+" " + (String) context.get(FacilioConstants.ContextNames.ORDER_TYPE);
		select.orderBy(orderBy);
		return select;
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
	
	public static int getReportType(long reportId) throws Exception
	{
		
		FacilioModule module = ModuleFactory.getReportModule();
		
		FacilioField reportTypeField=FieldFactory.getField("type", "REPORT_TYPE", module, FieldType.NUMBER);
		List<FacilioField> selectFields= Collections.singletonList(reportTypeField);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													.select(selectFields)
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportId, module))
													;
		
		
		int reportTypeInt=-1;
		List<Map<String, Object>>props =selectBuilder.get();
		if(props!=null&&!props.isEmpty())
		{
			List<ReportContext> report=FieldUtil.getAsBeanListFromMapList(props, ReportContext.class);
			if(report!=null&&!report.isEmpty())
			{
				reportTypeInt=report.get(0).getType();
			}
			
			
		}
		

		return reportTypeInt;
	}
	public static String getReportModuleName(long reportId) throws Exception {
		ReportContext reportContext =  ReportUtil.getReport(reportId,true);
		return reportContext.getModule().getName();
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
				if (report.getWorkflowId() != null && report.getWorkflowId() > 0) {
					report.setTransformWorkflow(WorkflowUtil.getWorkflowContext(report.getWorkflowId(), true));
				}
				if(report.getDataPoints() != null && report.getDataPoints().size() > 0){
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

						if (CollectionUtils.isNotEmpty(dataPoint.getHavingCriteria())) {
							for (ReportHavingContext reportHavingContext : dataPoint.getHavingCriteria()) {
								FacilioField field = getField(reportHavingContext.getFieldId(), xAxis.getModuleName(), reportHavingContext.getFieldName(), modBean);
								reportHavingContext.setField(field);
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
	
	public static long addReport(ReportContext reportContext, Context context) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getReportModule().getTableName())
													.fields(FieldFactory.getReport1Fields());
		reportContext.setCreatedTime(System.currentTimeMillis());
		reportContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext);
		Long appId = context.containsKey("dashboard_clone") ? (Long) context.get("target_app_id") : reportContext.getAppId() > 0 ? reportContext.getAppId() : AccountUtil.getCurrentUser().getApplicationId();
		props.put("appId",appId);
		long id = insertBuilder.insert(props);
		reportContext.setId(id);
		
		return id;
	}

	public static long shareReport(SingleSharingContext reportShareContext) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReportShareModule().getTableName())
				.fields(FieldFactory.getReportShareField());
		Map<String, Object> props = FieldUtil.getAsProperties(reportShareContext);
		return insertBuilder.insert(props);
	}

	public static void deleteReportShareRecords(Long parentId) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getReportShareModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
		deleteBuilder.delete();
	}

	public static List<Map<String, Object>> getReportShareDetails(Long parentId) throws Exception {
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getReportShareModule().getTableName())
				.select(FieldFactory.getReportShareField())
		        .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = select.get();
		return props;
	}

	public static boolean updateReport(ReportContext reportContext) throws Exception {
		
		FacilioModule module = ModuleFactory.getReportModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getReport1Fields())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(reportContext.getId(), module))
													.ignoreSplNullHandling()
													;
		reportContext.setModifiedTime(System.currentTimeMillis());
		reportContext.setModifiedBy(AccountUtil.getCurrentUser().getId());
		Map<String, Object> props = FieldUtil.getAsProperties(reportContext, true);
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
		if(removedWidgets.size() > 0) {
			deleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName() + ".ID", "id", StringUtils.join(removedWidgets, ","), StringOperators.IS));

			deleteRecordBuilder.delete();
		}
		
		
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
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if(CriteriaAPI.getCondition(fieldMap.get("moduleId"), moduleIds,NumberOperators.EQUALS) == null)
		{
			return reportFolders;
		}

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(fields)
													.table(reportFoldermodule.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportFoldermodule))
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), moduleIds,NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(fieldMap.get("folderType"),
															String.valueOf(FolderType.PIVOT.getValue()), PickListOperators.ISN_T));
		
		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}
		
		List<Map<String, Object>> props = select.get();
		if(props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				
				ReportFolderContext folder = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
				if(isWithReports) {
					List<ReportContext> response_folder_list = new ArrayList<>();
					List<ReportContext> reports = getReportsFromFolderId(folder.getId());
					if(reports != null && reports.size() > 0)
					{
						ReportContext  new_report_context = null;
						for(ReportContext report_context : reports)
						{
							new_report_context = new ReportContext();
							new_report_context.setName(report_context.getName());
							new_report_context.setId(report_context.getId());
							new_report_context.setReportFolderId(report_context.getReportFolderId());
							new_report_context.setModule(report_context.getModule());
							new_report_context.setCreatedBy(report_context.getCreatedBy());
							new_report_context.setModifiedTime(report_context.getModifiedTime());
							new_report_context.setModifiedBy(report_context.getModifiedBy());
							new_report_context.setModuleName(report_context.getModule().getName());
							response_folder_list.add(new_report_context);
						}
					}
					folder.setReports(response_folder_list);
				}
				reportFolders.add(folder);
			}
			
		}
		Map<Long, SharingContext<SingleSharingContext>> map = SharingAPI.getSharingMap(ModuleFactory.getReportSharingModule(), SingleSharingContext.class);
		for (int i = 0; i < reportFolders.size(); i++) {
			
			if (map.containsKey(reportFolders.get(i).getId())) {
				
				reportFolders.get(i).setReportSharing(map.get(reportFolders.get(i).getId()));;	
			}
		}
		return getFilteredReport(reportFolders);
	}
	public static List<ReportFolderContext> getAllCustomModuleReportFolderNew(String searchText) throws Exception {
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
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if(CriteriaAPI.getCondition(fieldMap.get("moduleId"), moduleIds,NumberOperators.EQUALS) == null)
		{
			return reportFolders;
		}

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(reportFoldermodule.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(reportFoldermodule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), moduleIds,NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("folderType"),
						String.valueOf(FolderType.PIVOT.getValue()), PickListOperators.ISN_T));

		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}

		List<Map<String, Object>> props = select.get();
		if(props != null && !props.isEmpty()) {

			for(Map<String, Object> prop :props) {

				ReportFolderContext folder = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
				reportFolders.add(folder);
			}

		}
		Map<Long, SharingContext<SingleSharingContext>> map = SharingAPI.getSharingMap(ModuleFactory.getReportSharingModule(), SingleSharingContext.class);
		for (int i = 0; i < reportFolders.size(); i++) {

			if (map.containsKey(reportFolders.get(i).getId())) {

				reportFolders.get(i).setReportSharing(map.get(reportFolders.get(i).getId()));;
			}
		}
		return getFilteredReport(reportFolders);
	}
	public static Long createFolder(String name, Integer folderType, long appid, String module) throws Exception{
		ReportFolderContext reportFolder = new ReportFolderContext();
		reportFolder.setName(name);
		if(folderType != null && folderType >0){
			reportFolder.setFolderType(folderType);
		}
		reportFolder.setAppId(appid);
		FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
		FacilioContext context = chain.getContext();
		context.put("actionType", "ADD");
		context.put("moduleName",module);
		context.put("reportFolder", reportFolder);
		chain.execute();
		return reportFolder.getId();
	}
	public static Long checkFolder(ReportContext reportContext, Long target_app_id,Integer folder_type, String moduleName) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReport1FolderFields())
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(target_app_id), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PARENT_FOLDER_ID", "parentFolderId", "NULL", CommonOperators.IS_EMPTY));
				if(folder_type != null && folder_type > 0) {
					selectBuilder.andCondition(CriteriaAPI.getCondition("FOLDER_TYPE", "folderType", String.valueOf(folder_type), NumberOperators.EQUALS))
							.andCustomWhere("MODULEID=?",module.getModuleId());
				} else if (folder_type == null && moduleName.equals("energydata")) {
					selectBuilder.andCondition(CriteriaAPI.getCondition("FOLDER_TYPE", "folderType", "NULL", CommonOperators.IS_EMPTY))
							.andCustomWhere("MODULEID=?",module.getModuleId());
				}
				else{
					selectBuilder.andCondition(CriteriaAPI.getCondition("FOLDER_TYPE", "folderType", "NULL", CommonOperators.IS_EMPTY))
							.andCustomWhere("MODULEID=?",reportContext.getModuleId());
				}
		List<Map<String, Object>> field = selectBuilder.get();
		if (field != null && field.size() > 0) {
			ReportFolderContext reportFolder = (ReportFolderContext) FieldUtil.getAsBeanFromMap(field.get(0), ReportFolderContext.class);
			if(reportFolder == null){
				return null;
			}
			return reportFolder.getId();
		}else {
			Long folderId = createFolder("Default Folder", folder_type, target_app_id, reportContext.getModule().getName());
			if (folderId == null || folderId <= 0) {
				return null;
			}
			return folderId;
		}
	}
	public static Long cloneReport(Long reportId, Long target_app_id, Long cloned_app_id) throws Exception{
		FacilioContext newcontext = new FacilioContext();
		ReportContext reportContext = ReportUtil.getReport(reportId);
		reportContext.setId(-1);
		newcontext.put("cloned_app_id", cloned_app_id);
		newcontext.put("target_app_id", target_app_id);
		newcontext.put("dashboard_clone", true);
		if(reportContext.getTypeEnum() == ReportContext.ReportType.PIVOT_REPORT) {
			Long folderId = checkFolder(reportContext,target_app_id,3, "energydata");
			reportContext.setReportFolderId(folderId);
			newcontext.put(FacilioConstants.ContextNames.REPORT, reportContext);
			clonePivot(newcontext, reportContext);
		}
		else if (reportContext.getTypeEnum() == ReportContext.ReportType.WORKORDER_REPORT) {
			Long folderId = checkFolder(reportContext,target_app_id, null, "workorder");
			reportContext.setReportFolderId(folderId);
			newcontext.put(FacilioConstants.ContextNames.REPORT, reportContext);
			cloneWorkOrderReport(newcontext);
		}
		else {
			Long folderId = checkFolder(reportContext,target_app_id, null,"energydata");
			reportContext.setReportFolderId(folderId);
			newcontext.put(FacilioConstants.ContextNames.REPORT, reportContext);
			cloneAnalyticsReport(newcontext);
		}
		return reportContext.getId();
	}
	public static void clonePivot(FacilioContext newcontext, ReportContext reportContext) throws Exception{
		FacilioChain chain = TransactionChainFactory.addOrUpdatePivotReport();
		JSONParser parser = new JSONParser();
		ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson(
				(JSONObject) parser.parse(reportContext.getTabularState()), ReportPivotParamsContext.class);

		newcontext.put(FacilioConstants.Reports.ROWS, pivotparams.getRows());
		newcontext.put(FacilioConstants.Reports.DATA, pivotparams.getData());
		newcontext.put(FacilioConstants.ContextNames.VALUES, pivotparams.getValues());
		newcontext.put(FacilioConstants.ContextNames.FORMULA, pivotparams.getFormula());
		newcontext.put(FacilioConstants.ContextNames.MODULE_NAME, pivotparams.getModuleName());
		newcontext.put(FacilioConstants.ContextNames.CRITERIA, pivotparams.getCriteria());
		newcontext.put(FacilioConstants.ContextNames.SORTING, pivotparams.getSortBy());
		newcontext.put(FacilioConstants.ContextNames.TEMPLATE_JSON, pivotparams.getTemplateJSON());
		newcontext.put(FacilioConstants.ContextNames.DATE_FIELD, pivotparams.getDateFieldId());
		newcontext.put(FacilioConstants.ContextNames.DATE_OPERATOR, pivotparams.getDateOperator());
		newcontext.put(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, pivotparams.getShowTimelineFilter());
		newcontext.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, pivotparams.getDateValue());
		newcontext.put(FacilioConstants.ContextNames.IS_BUILDER_V2, pivotparams.isBuilderV2());
		newcontext.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, false);
		newcontext.put(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
		newcontext.put(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
		chain.execute(newcontext);
	}
	public static void cloneWorkOrderReport(FacilioContext newcontext) throws Exception{
		ReportContext reportContext = (ReportContext) newcontext.get(FacilioConstants.ContextNames.REPORT);
		addReport(reportContext, newcontext);
	}
	public static void cloneAnalyticsReport(FacilioContext newcontext) throws Exception{
		ReportContext report = (ReportContext) newcontext.get(FacilioConstants.ContextNames.REPORT);
		report.setWorkflowId(null);
		if (report.getTransformWorkflow() != null) {
			long workflowId = WorkflowUtil.addWorkflow(report.getTransformWorkflow());
			report.setWorkflowId(workflowId);
		}
		addReport(report, newcontext);
		Set<ReportFieldContext> reportFields = new HashSet<>();
		for(ReportDataPointContext dataPoint : report.getDataPoints()) {
			if (dataPoint.getTypeEnum() == ReportDataPointContext.DataPointType.DERIVATION) {
				continue;
			}

			if(dataPoint.getxAxis() != null)  {
				reportFields.add(constructReportField(dataPoint.getxAxis().getModule(), dataPoint.getxAxis().getField(), report.getId()));
			}

			if(dataPoint.getyAxis() != null) {
				reportFields.add(constructReportField(dataPoint.getyAxis().getModule(), dataPoint.getyAxis().getField(), report.getId()));
			}

			if(dataPoint.getDateField() != null) {
				reportFields.add(constructReportField(dataPoint.getDateField().getModule(), dataPoint.getDateField().getField(), report.getId()));
			}
		}
		if (report.getFilters() != null && !report.getFilters().isEmpty()) {
			for (ReportFilterContext filter : report.getFilters()) {
				reportFields.add(constructReportField(filter.getModule(), filter.getField(), report.getId()));
			}
		}

		ReportUtil.addReportFields(reportFields);
	}
	public static ReportFieldContext constructReportField (FacilioModule module, FacilioField field, long reportId) {
		ReportFieldContext reportFieldContext = new ReportFieldContext();
		reportFieldContext.setReportId(reportId);

		if (field.getFieldId() > 0) {
			reportFieldContext.setFieldId(field.getFieldId());
		}
		else if (field.getModule() != null && field.getModule().getName() != null && !field.getModule().getName().isEmpty() && field.getName() != null && !field.getName().isEmpty()){
//			reportFieldContext.setModuleName(field.getModule().getName());
			reportFieldContext.setField(module, field);
		}
		else {
			throw new IllegalArgumentException("Invalid field object for ReportFields addition");
		}
		return reportFieldContext;
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
	
	public static Boolean isSpaceAggregation(AggregateOperator aggr)
	{
		for(AggregateOperator operator : SpaceAggregateOperator.values()) {
            if( operator.equals(aggr))
            {
            	return true;
            }
        }
	
		return false;
	}
	public static Boolean isDateAggregateOperator(AggregateOperator aggr)
	{
		for(AggregateOperator operator : DateAggregateOperator.values()) {
            if( operator.equals(aggr))
            {
            	return true;
            }
        }
	
		return false;
	}
	public static FacilioField getField(ModuleBean modBean, Object fieldId,FacilioModule module) throws Exception {
		FacilioField xField = null;
		if (fieldId instanceof Long) {
			xField = modBean.getField((Long) fieldId);
		} else if (fieldId instanceof String) {
			xField = modBean.getField((String) fieldId, module.getName());
			if (xField == null) {
				xField = ReportFactory.getReportField((String) fieldId);
				if(xField == null 
						&& FieldFactory.isSystemField((String) fieldId) 
						&& module.getName().equalsIgnoreCase(FacilioConstants.ContextNames.WORK_ORDER)) 
				{
					xField = FieldFactory.getSystemField((String) fieldId, module);
				}
			}
		}
		return xField;
	}
	public static List<ReportFolderContext> getAllReportFolderNew(String moduleName, String searchText, Boolean isPivot, Long webTabId, Context contexts) throws Exception {
		String name = AccountUtil.getCurrentApp().getLinkName();
		boolean isMainApp =  name.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		long appId = (long) contexts.get(FacilioConstants.ContextNames.APP_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if(isMainApp) {
			FacilioModule module = modBean.getModule(moduleName);
			reportFolders.addAll(ReportUtil.getReportsFoldersNew(module.getModuleId(), searchText, true, isPivot, appId));
			if(moduleName != null && (moduleName.equals("newreadingalarm") || moduleName.equals("bmsalarm"))){
				FacilioModule newmodule = modBean.getModule("alarm");
				reportFolders.addAll(ReportUtil.getReportFolders(newmodule.getModuleId(), searchText, isMainApp, isPivot));
			}
		}
		else
		{
			Boolean isAnalyticsReport = false;
			List<Long> moduleIds = new ArrayList<>();
			if(webTabId != null && webTabId > 0)
			{
				WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
				WebTabContext webTabContext = tabBean.getWebTab(webTabId);
				JSONObject config = webTabContext.getConfigJSON();
				if(config != null && config.containsKey("type") && config.get("type").equals("module_reports"))
				{
					List<Long> webtab_moduleIds = ApplicationApi.getModuleIdsForTab(webTabContext.getId());
					if(webtab_moduleIds != null && webtab_moduleIds.size() > 0)
					{
						for(Long moduleId : webtab_moduleIds)
						{
							FacilioModule module = modBean.getModule(moduleId);
							if(module != null && !module.isCustom())
							{
								Set<FacilioModule> subModules = ReportFactoryFields.getSubModulesList(module.getName());
								if(subModules != null){
									moduleIds.addAll(subModules.stream().map(m -> m.getModuleId()).collect(Collectors.toList()));
								}
							}
							if(module != null) {
								moduleIds.add(moduleId);
							}
						}
					}
				}
				else if((config != null && config.containsKey("type") && config.get("type").equals("analytics_reports")) || isPivot){
					FacilioModule energyData = modBean.getModule("energydata");
					moduleIds.add(energyData.getModuleId());
					isAnalyticsReport = true;
				}
			}
			else if(isPivot)
			{
				FacilioModule energyData = modBean.getModule("energydata");
				moduleIds.add(energyData.getModuleId());
			}
			else if( appId != 0 )
			{
				if(moduleName != null && (moduleName.equals("energydata") || moduleName.equals("energyData"))){
					FacilioModule energyData = modBean.getModule("energydata");
					moduleIds.add(energyData.getModuleId());
					isAnalyticsReport = true;
				}
				else
				{
					List<WebTabContext> webtabs = ApplicationApi.getWebTabsForApplication(appId);
					for (WebTabContext webtab : webtabs) {
						if (webtab.getTypeEnum() == WebTabContext.Type.MODULE) {
							moduleIds.addAll(ApplicationApi.getModuleIdsForTab(webtab.getId()));
						} else if (!isPivot && webtab.getTypeEnum() == WebTabContext.Type.REPORT && (webtab.getConfigJSON() != null &&
								webtab.getConfigJSON().containsKey("type") && webtab.getConfigJSON().get("type").equals("analytics_reports"))) {
							isAnalyticsReport = true;
						}
					}
				}
			}
			if(moduleIds != null && !moduleIds.isEmpty())
			{
				FacilioChain chain = ReadOnlyChainFactory.getAutomationModules();
				FacilioContext context = chain.getContext();
				chain.execute();
				List<FacilioModule> modules = (ArrayList) context.get(FacilioConstants.ContextNames.MODULE_LIST);
				List<Long> moduleIds_list = new ArrayList<>();
				for(FacilioModule module_obj : modules){
					moduleIds_list.add(module_obj.getModuleId());
				}
				if(isPivot || isAnalyticsReport)
				{
					FacilioModule energyData = modBean.getModule("energydata");
					moduleIds_list.add(energyData.getModuleId());
				}
				if(moduleName != null && moduleName.equals("alarm")){
					FacilioModule alarm_module = modBean.getModule(moduleName);
					if(alarm_module != null){
						moduleIds.add(alarm_module.getModuleId());
						moduleIds_list.add(alarm_module.getModuleId());
						FacilioModule new_alarm_module = modBean.getModule("newreadingalarm");
						if(new_alarm_module != null && !moduleIds.contains(new_alarm_module.getModuleId()))
						{
							moduleIds.add(new_alarm_module.getModuleId());
						}
					}
				}
				List<Long> newList = moduleIds.stream().distinct().collect(Collectors.toList());
				for(long moduleId : newList)
				{
					if(moduleIds_list != null) {
						reportFolders.addAll(ReportUtil.getReportsFoldersNew(moduleId, searchText, false, isPivot, appId));
					}
				}
			}
		}
		return getFilteredReport(reportFolders);
	}

	public static List<ReportFolderContext> getReportsFoldersNew(Long moduleId, String searchText, Boolean isMainApp, Boolean isPivot, long appId)throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule othermodule = modBean.getModule(moduleId);
		List<FacilioField> otherfields = FieldFactory.getReport1FolderFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(otherfields);

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(otherfields)
				.table(ModuleFactory.getReportFolderModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(othermodule.getModuleId()), NumberOperators.EQUALS));

		if(isPivot)
		{
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("folderType"), String.valueOf(FolderType.PIVOT.getValue()), PickListOperators.IS));
			if(isMainApp){
				select.andCriteria(ReportUtil.getReportAppIdCriteria(fieldMap, appId));
			} else{
				select.andCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(appId), NumberOperators.EQUALS));
			}
		}
		else
		{
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("folderType"), String.valueOf(FolderType.PIVOT.getValue()), PickListOperators.ISN_T));
			Criteria addAppIdCriteria = ReportUtil.getReportAppIdCriteria(fieldMap, appId);
			select.andCriteria(addAppIdCriteria);
		}
		if (searchText != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), searchText, StringOperators.CONTAINS));
		}

		List<Map<String, Object>> otherprops = select.get();
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if (otherprops != null && !otherprops.isEmpty()) {

			for (Map<String, Object> prop : otherprops) {
				ReportFolderContext folder = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
				reportFolders.add(folder);
			}
		}
		Map<Long, SharingContext<SingleSharingContext>> othermap = SharingAPI.getSharingMap(ModuleFactory.getReportSharingModule(), SingleSharingContext.class);
		for (int i = 0; i < reportFolders.size(); i++) {

			if (othermap.containsKey(reportFolders.get(i).getId())) {
				reportFolders.get(i).setReportSharing(othermap.get(reportFolders.get(i).getId()));
			}
		}
		return reportFolders;
	}
	public  static JSONObject getPageParams(ReportContext report) throws Exception {
		JSONObject pageParams = new JSONObject();
		JSONObject dateRange = new JSONObject();
		JSONObject filters = new JSONObject();
		pageParams.put("reportId",report.getId());
		if(report.getDateRange() != null){
			dateRange.put("startTime",report.getDateRange().getStartTime());
			dateRange.put("endTime", report.getDateRange().getEndTime());
			dateRange.put("operatorId",20l);
			pageParams.put("dateRange", ReportsUtil.encodeURIComponent(dateRange.toJSONString()));
		}
		if(CollectionUtils.isNotEmpty(report.getUserFilters())){
			for(ReportUserFilterContext filter : report.getUserFilters()){
				JSONObject userFilter = new JSONObject();
				userFilter.put("operatorId",36l);
				if(filter.getDefaultValues() != null){
					userFilter.put("value",filter.getDefaultValues());
				}
				else {
					userFilter.put("value",filter.getChooseValue().getValues());
				}
				filters.put(filter.getName(),userFilter);
			}
			pageParams.put("filters",ReportsUtil.encodeURIComponent(filters.toJSONString()));
		}
		return pageParams;
	}
	public static String getAppBaseURL() {
		try {
			HttpServletRequest req = ServletActionContext.getRequest();
			if (req != null) {
				return FacilioProperties.getAppProtocol() + "://" + req.getServerName();
			}
		} catch (Exception e) {}
		return FacilioProperties.getAppProtocol() + "://" + FacilioProperties.getAppDomain();
	}
	public  static String getFileUrl(ReportContext report, FileInfo.FileFormat fileFormat, String fileName) throws Exception {
		long fileId = 0;
		String linkName = null;
		String pageName;
		if(report.getTypeEnum() == ReportContext.ReportType.WORKORDER_REPORT){
			pageName = "modulereport";
		}
		else{
			pageName = "readingReport";
		}
		Long appId = report.getAppId();
		JSONObject pageParams = getPageParams(report);
		Map<String, Object> result = new HashMap<>();
		PDFOptions pdfOptions = FieldUtil.getAsBeanFromMap(result, PDFOptions.class);
		if(appId != null && appId != -1 && appId != 0){
			linkName = ApplicationApi.getApplicationForId(appId).getLinkName();
		}
		else {
			linkName = FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP;
		}
		if (fileFormat.equals(FileInfo.FileFormat.PDF)) {
			fileId = PDFServiceFactory.getPDFService().exportPage(fileName, linkName, pageName, pageParams, PDFService.ExportType.PDF,pdfOptions);
			LOGGER.info("fileId"+fileId);
		}
		else if (fileFormat.equals(FileInfo.FileFormat.IMAGE)) {
			fileId = PDFServiceFactory.getPDFService().exportPage(fileName, linkName, pageName, pageParams, PDFService.ExportType.SCREENSHOT, pdfOptions);
		}
		return getAppBaseURL() + PublicFileUtil.createPublicFileUrl(fileId);

	}

	public static void getScheduledInfo1Rel(List<ReportInfo> reportInfo)throws Exception
	{
		if(reportInfo != null && reportInfo.size() > 0)
		{
			GenericSelectRecordBuilder select = null;

			for(ReportInfo report_info : reportInfo)
			{
				select = new GenericSelectRecordBuilder()
						.select(FieldFactory.getReportScheduleInfo1RelFields())
						.table(ModuleFactory.getReportScheduleInfoRel().getTableName())
						.andCondition(CriteriaAPI.getCondition("SCHEDULED_ID", "scheduled_id", report_info.getId() + "", NumberOperators.EQUALS));
				List<Map<String, Object>> lists = select.get();
				if(lists != null && lists.size() > 0)
				{
					for(Map<String, Object> prop : lists)
					{
						V3ScheduledReportRelContext scheduledRelContext = FieldUtil.getAsBeanFromMap(prop, V3ScheduledReportRelContext.class);
						if(scheduledRelContext != null && scheduledRelContext.getReportId() > 0) {
							report_info.getSelected_reportIds().add(scheduledRelContext.getReportId());
						}
					}
				}

			}
		}
	}

	public static HashMap<String, Object> sortPivotTableData(Context context)throws Exception
	{
		HashMap<String, Object> dataresult = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA);
		JSONObject sortBy = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
		if (sortBy != null && sortBy.containsKey("limit") && dataresult != null && dataresult.containsKey("records"))
		{
			Long limit = sortBy.get("limit") instanceof Integer ? Long.valueOf((Integer) sortBy.get("limit")) : (Long) sortBy.get("limit");
			String sort_alias = (String) sortBy.get("alias");
			Long order = sortBy.get("order") instanceof Integer ? Long.valueOf((Integer) sortBy.get("order")) : (Long) sortBy.get("order");
			List<LinkedHashMap<String, HashMap>> records_list = (ArrayList<LinkedHashMap<String, HashMap>>) dataresult.get("records");
			if (sort_alias != null && order != null) {
				HashMap<String, FacilioField> pivotVs_Alias = (HashMap<String, FacilioField>) context.get(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD);
				if (pivotVs_Alias != null && pivotVs_Alias.containsKey(sort_alias)) {
					if (pivotVs_Alias.get(sort_alias) != null && (pivotVs_Alias.get(sort_alias) instanceof LookupField)) {
						Collections.sort(records_list, new HashMapValueComparator(sort_alias, true, order));
					} else {
						Collections.sort(records_list, new HashMapValueComparator(sort_alias, false, order));
					}
					if(records_list != null && records_list.size() > 0){
						int counter=1;
						for(LinkedHashMap<String, HashMap> record : records_list)
						{
							if(record != null && record.containsKey("number"))
							{
								JSONObject formatted_val = new JSONObject();
								formatted_val.put("formattedValue", counter++);
								record.put("number", formatted_val);
							}
						}
					}
				}
			}
			if (records_list != null)
			{
				Boolean isExport = (Boolean ) context.get("isExport");
				if(isExport != null && isExport){
					dataresult.put("records", records_list);
				}else{
					List<LinkedHashMap> firstNElementsList = records_list.stream().limit(limit).collect(Collectors.toList());
					dataresult.put("records", firstNElementsList);
				}
				return dataresult;
			} else {
				return dataresult;
			}
		}
		return dataresult;
	}
}