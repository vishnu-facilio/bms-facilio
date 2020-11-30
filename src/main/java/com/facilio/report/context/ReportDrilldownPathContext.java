package com.facilio.report.context;

import org.json.simple.JSONObject;

import com.facilio.modules.AggregateOperator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportDrilldownPathContext  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSONObject xField;
//	private String selectedPrevXValues;
//	public String getSelectedPrevXValues() {
//		return selectedPrevXValues;
//	}
//
//
//	public void setSelectedPrevXValues(String selectedPrevXValues) {
//		this.selectedPrevXValues = selectedPrevXValues;
//	}
//
//
//	public String getSelectedPrevYAlias() {
//		return selectedPrevYAlias;
//	}
//
//
//	public void setSelectedPrevYAlias(String selectedPrevYAlias) {
//		this.selectedPrevYAlias = selectedPrevYAlias;
//	}
//
//	private String selectedPrevYAlias;
//
//




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
	
	@JsonIgnore
	public AggregateOperator getxAggrEnum() {
		return xAggr;
	}

	public void setxAggr(AggregateOperator xAggr) {
		this.xAggr = xAggr;
	}


}
