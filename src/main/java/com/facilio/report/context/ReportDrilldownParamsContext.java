package com.facilio.report.context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReportDrilldownParamsContext {

	
	
	private  JSONArray criteria;
	public JSONArray getCriteria() {
		return criteria;
	}
	public void setCriteria(JSONArray criteria) {
		this.criteria = criteria;
	}
	public JSONObject getxField() {
		return xField;
	}
	public void setxField(JSONObject xField) {
		this.xField = xField;
	}
	private JSONObject xField;


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
