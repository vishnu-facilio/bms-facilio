package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportAnalysisContext;
import com.facilio.report.context.ReportAnalysisContext.ReportMode;

public class V2ReportAction extends FacilioAction {
	public String fetchReadingsData() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DATE_RANGE, range);
			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, fields);
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
	
	private DateRange range;
	public DateRange getRange() {
		return range;
	}
	public void setRange(DateRange range) {
		this.range = range;
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
	
	private List<ReportAnalysisContext> fields;
	public List<ReportAnalysisContext> getFields() {
		return fields;
	}
	public void setFields(List<ReportAnalysisContext> fields) {
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