package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.WorkorderAnalysisContext;

public class V2ReportAction extends FacilioAction {
	public String fetchReadingsData() {
		try {
			
			JSONParser parser = new JSONParser();
			JSONArray fieldArray = (JSONArray) parser.parse(fields);
			JSONArray baseLineList = null;
			if (baseLines != null && !baseLines.isEmpty()) {
				baseLineList = (JSONArray) parser.parse(baseLines);
			}
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.START_TIME, startTime);
			context.put(FacilioConstants.ContextNames.END_TIME, endTime);
			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
			context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
			context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
			
			Chain fetchReadingDataChain = ReadOnlyChainFactory.fetchReadingReportChain();
			fetchReadingDataChain.execute(context);
			
			return setReportResult(context);
		}
		catch (Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
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
	
	public String fetchWorkorderData() {
		try {
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
		catch (Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
	}
	
	private String setReportResult(FacilioContext context) {
		setResult("report", context.get(FacilioConstants.ContextNames.REPORT));
		setResult("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES));
		setResult("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
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
}