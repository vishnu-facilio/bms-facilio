package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportAnalysisContext;
import com.facilio.report.context.ReportAnalysisContext.ReportMode;

public class V2ReportAction extends FacilioAction {
	public String fetchReadingsData() {
		try {
			
			JSONParser parser = new JSONParser();
			JSONArray fieldArray = (JSONArray) parser.parse(fields);
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.START_TIME, startTime);
			context.put(FacilioConstants.ContextNames.END_TIME, endTime);
			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReportAnalysisContext.class));
			context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
			
			Chain fetchReadingDataChain = ReadOnlyChainFactory.fetchReadingReportChain();
			fetchReadingDataChain.execute(context);
			
			setResult("report", context.get(FacilioConstants.ContextNames.REPORT));
			setResult("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES));
			setResult("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
			return SUCCESS;
		}
		catch (Exception e) {
			setResponseCode(1);
			setMessage(e);
			return ERROR;
		}
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
	
	private String fields;
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}

	private ReportAnalysisContext.ReportMode mode = ReportMode.TIMESERIES;
	public int getMode() {
		if (mode != null) {
			return mode.getValue();
		}
		return -1;
	}
	public void setMode(int mode) {
		this.mode = ReportMode.valueOf(mode);
	}
}