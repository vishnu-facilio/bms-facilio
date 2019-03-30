package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UnitAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JSONObject metricWithUnits;
	public JSONObject getMetricWithUnits() {
		return metricWithUnits;
	}

	public void setMetricWithUnits(JSONObject metricWithUnits) {
		this.metricWithUnits = metricWithUnits;
	}

	List<OrgUnitsContext> orgUnitsList;
	
	public List<OrgUnitsContext> getOrgUnitsList() {
		return orgUnitsList;
	}
	Collection<Metric> metrics;
	public Collection<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(Collection<Metric> metrics) {
		this.metrics = metrics;
	}

	public void setOrgUnitsList(List<OrgUnitsContext> orgUnitsList) {
		this.orgUnitsList = orgUnitsList;
	}

	public String getDefaultMetricUnits() throws Exception {
		
		Map<Metric, Collection<Unit>> metricWithUnit = Unit.getMetricUnitMap();
		
		metricWithUnits = new JSONObject();
		metrics = Metric.getAllMetrics();
		for( Metric metric :metricWithUnit.keySet()) {
			
			Collection<Unit> units = metricWithUnit.get(metric);
			
			JSONArray unitsJson = new JSONArray();
			for(Unit unit :units) {
				unitsJson.add(unit);
			}
			metricWithUnits.put(metric, unitsJson);
		}
		
		orgUnitsList = UnitsUtil.getOrgUnitsList();
		
		return SUCCESS;
	}
	
	JSONObject metricUnitMap;
	public JSONObject getMetricUnitMap() {
		return metricUnitMap;
	}

	public void setMetricUnitMap(JSONObject metricUnitMap) {
		this.metricUnitMap = metricUnitMap;
	}
	
	public String updatedefaultMetricUnits() throws Exception {
		
		if(metricUnitMap != null) {
			UnitsUtil.updateOrgUnitsList(metricUnitMap);
		}
		return SUCCESS;
	}
	int unit = -1;
	int metric = -1;
	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public int getMetric() {
		return metric;
	}

	public void setMetric(int metric) {
		this.metric = metric;
	}

	public String updatedefaultMetricUnit() throws Exception {
		
		if(unit > 0 && metric >0) {
			UnitsUtil.updateOrgUnit(metric, unit);
		}
		return SUCCESS;
	}
}
