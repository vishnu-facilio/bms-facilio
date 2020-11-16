package com.facilio.report.context;

import org.json.simple.JSONObject;

import com.facilio.modules.AggregateOperator;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ReportDrillDownContext  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSONObject xField;
	
	public JSONObject getxField() {
		return xField;
	}

	public void setxField(JSONObject xField) {
		this.xField = xField;
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

	public AggregateOperator getxAggrEnum() {
		return xAggr;
	}

	public void setxAggr(AggregateOperator xAggr) {
		this.xAggr = xAggr;
	}


}
