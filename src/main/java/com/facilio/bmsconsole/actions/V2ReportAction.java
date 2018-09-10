package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.context.WorkorderAnalysisContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.tasker.ScheduleInfo;

public class V2ReportAction extends FacilioAction {
	
	private ReportContext reportContext;
	public ReportContext getReportContext() {
		return reportContext;
	}
	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}
	
	private long reportId = -1;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String fetchReportFolders() throws Exception {
		
		List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(moduleName, true);
		setResult("reportFolders", reportFolders);
		setResult("moduleName", moduleName);
		return SUCCESS;
	}
	
	public String fetchReport() throws Exception {
		reportContext = ReportUtil.getReport(reportId);
		setResult("report", reportContext);
		return SUCCESS;
	}
	
	public String fetchReportWithData() throws Exception {
		
		FacilioContext context = new FacilioContext();
		setReportWithDataContext(context);
		
		Chain fetchReadingDataChain = ReadOnlyChainFactory.fetchReportDataChain();
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
	}
	
	private void setReportWithDataContext(FacilioContext context) throws Exception {
		reportContext = ReportUtil.getReport(reportId);
		
		if(startTime > 0 && endTime > 0) {
			reportContext.setDateRange(new DateRange(startTime, endTime));
			reportContext.setDateValue(new DateRange(startTime, endTime).toString());
		}
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
	}
	
	ReportFolderContext reportFolder;
	
	public ReportFolderContext getReportFolder() {
		return reportFolder;
	}
	public void setReportFolder(ReportFolderContext reportFolder) {
		this.reportFolder = reportFolder;
	}
	public String addReportFolder() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		reportFolder.setOrgId(AccountUtil.getCurrentOrg().getId());
		reportFolder.setModuleId(module.getModuleId());
		
		reportFolder = ReportUtil.addReportFolder(reportFolder);
		setResult("reportFolder", reportFolder);
		return SUCCESS;
	}
	
	public String addReadingReport() throws Exception {
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
		context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
		context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
		context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		Chain addReadingReport = FacilioChainFactory.addReadingReportChain();
		addReadingReport.execute(context);
		
		return setReportResult(context);
	}
	
	public String addWorkOrderReport() throws Exception {
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperator);
		context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
		context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
		context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
		context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		Chain addWorkOrderChain = FacilioChainFactory.addWorkOrderReportChain();
		addWorkOrderChain.execute(context);
		
		return setReportResult(context);
	}
	
	public String fetchReadingsData() throws Exception {
		FacilioContext context = new FacilioContext();
		setReadingsDataContext(context);
		
		Chain fetchReadingDataChain = ReadOnlyChainFactory.fetchReadingReportChain();
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
	}

	public boolean deleteWithWidget;
	
	public boolean isDeleteWithWidget() {
		return deleteWithWidget;
	}
	public void setDeleteWithWidget(boolean deleteWithWidget) {
		this.deleteWithWidget = deleteWithWidget;
	}
	public String deleteReport() throws Exception {
		
		List<WidgetChartContext> widgetCharts = null;
		if(!deleteWithWidget) {
			widgetCharts = DashboardUtil.getWidgetFromDashboard(reportId,true);
		}
		if(widgetCharts == null || widgetCharts.isEmpty()) {
			ReportUtil.deleteReport(reportId);
			return SUCCESS;
		}
		else {
			setResult("errorString", "Report Used In Dashboard");
		}
		return SUCCESS;
	}
	
	private void setReadingsDataContext(FacilioContext context) throws Exception {
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		
		
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
		context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
	}
	
	private ReadingAnalysisContext.ReportMode mode = ReportMode.TIMESERIES;
	public int getMode() {
		if (mode != null) {
			return mode.getValue();
		}
		return -1;
	}
	public void setMode(int mode) {
		this.mode = ReportMode.valueOf(mode);
	}
	
	public String fetchWorkorderData() throws Exception {
		
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.REPORT_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, WorkorderAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
		
		Chain fetchReadingDataChain = ReadOnlyChainFactory.fetchWorkorderReportChain();
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
		
	}
	
	private String setReportResult(FacilioContext context) {
		setResult("report", context.get(FacilioConstants.ContextNames.REPORT));
		setResult("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES));
		setResult("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
		setResult("reportVarianceData", context.get(FacilioConstants.ContextNames.REPORT_VARIANCE_DATA));
		return SUCCESS;
	}
	
	private String fields;
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String chartState;
	public String tabularState;
	
	public String getChartState() {
		return chartState;
	}
	public void setChartState(String chartState) {
		this.chartState = chartState;
	}
	public String getTabularState() {
		return tabularState;
	}
	public void setTabularState(String tabularState) {
		this.tabularState = tabularState;
	}

	private AggregateOperator xAggr;
	public int getxAggr() {
		if (xAggr != null) {
			return xAggr.getValue();
		}
		return -1;
	}
	public void setxAggr(int xAggr) {
		this.xAggr = AggregateOperator.getAggregateOperator(xAggr);
	}
	
	private String baseLines;
	public String getBaseLines() {
		return baseLines;
	}
	public void setBaseLines(String baseLines) {
		this.baseLines = baseLines;
	}
	Integer dateOperator;
	String dateOperatorValue;
	public Integer getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(Integer dateOperator) {
		this.dateOperator = dateOperator;
	}
	public String getDateOperatorValue() {
		return dateOperatorValue;
	}
	public void setDateOperatorValue(String dateOperatorValue) {
		this.dateOperatorValue = dateOperatorValue;
	}
	
	
	private FileFormat fileFormat;
	public int getFileFormat() {
		if (fileFormat != null) {
			return fileFormat.getIntVal();
		}
		return -1;
	}
	public void setFileFormat(int fileFormat) {
		this.fileFormat = FileFormat.getFileFormat(fileFormat);
	}
	
	public String exportReport() throws Exception{
		
		FacilioContext context = new FacilioContext();
		
		Chain exportChain;
		if (reportId != -1) {
			exportChain = FacilioChainFactory.getExportReportFileChain();
			setReportWithDataContext(context);
		}
		else {
			exportChain = FacilioChainFactory.getExportReadingReportFileChain();
			setReadingsDataContext(context);
			context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		}
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		
		exportChain.execute(context);
		
		setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
		
		return SUCCESS;
	}
	
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	public String sendReportMail() throws Exception{

		FacilioContext context = new FacilioContext();
		Chain mailReportChain;
		if (reportId != -1) {
			mailReportChain = FacilioChainFactory.sendReportMailChain();
			setReportWithDataContext(context);
		}
		else {
			mailReportChain = FacilioChainFactory.sendReadingReportMailChain();
			setReadingsDataContext(context);
			context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		}
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		
		mailReportChain.execute(context);
		
		setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
		
		return SUCCESS;
	}
	
	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	
	private ScheduleInfo scheduleInfo;
	public ScheduleInfo getScheduleInfo() {
		return scheduleInfo;
	}
	public void setScheduleInfo(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = scheduleInfo;
	}
	
	public String scheduleReport() throws Exception{
		
		FacilioContext context = new FacilioContext();
		
		emailTemplate.setName("Report");
		emailTemplate.setFrom("report@${org.domain}.facilio.com");

		context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.MAX_COUNT, maxCount);
		context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, scheduleInfo);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
 		
		Chain scheduleReportChain = FacilioChainFactory.scheduleReportChain();
		scheduleReportChain.execute(context);
		
		setResult("id", context.get(FacilioConstants.ContextNames.RECORD_ID));
		
		return SUCCESS;
	}
	
}