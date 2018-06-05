package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.google.common.collect.Multimap;
import com.opensymphony.xwork2.ActionSupport;

public class UnitAction extends ActionSupport {

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
}
