package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.constants.FacilioConstants;

public class V2ReportAction extends FacilioAction {
	public String fetchReadingsData() {
		try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DATE_RANGE, range);
			context.put(FacilioConstants.ContextNames.PARENT_ID, parentId);
			context.put(FacilioConstants.ContextNames.READING_FIELD, fieldId);
			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_AGGR, yAggr);
			
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
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
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
	
	private AggregateOperator yAggr;
	public int getyAggr() {
		if (yAggr != null) {
			return yAggr.getValue();
		}
		return -1;
	}
	public void setyAggr(int yAggr) {
		this.yAggr = AggregateOperator.getAggregateOperator(yAggr);
	}
}