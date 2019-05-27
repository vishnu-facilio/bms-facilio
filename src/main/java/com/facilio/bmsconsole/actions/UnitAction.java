package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.opensymphony.xwork2.ActionSupport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.Collections;
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
	
	long fieldId;
	long parentId;
	
	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	long startTime;
	long endTime;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String unitMigration() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(fieldId <= 0 || parentId <= 0 || startTime <= 0 || endTime <= 0  || unit <= 0) {
			return null;
		}
		
		FacilioField field = modBean.getField(fieldId).clone();
		
		FacilioModule module = field.getModule();
		
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		
		Unit from = Unit.valueOf(unit);
		
		if(from.isSiUnit()) {
			return null;
		}
		
		SelectRecordsBuilder<ReadingContext> selectRecordBuilder = new SelectRecordsBuilder<ReadingContext>()
				.module(module)
				.beanClass(ReadingContext.class)
				.select(Collections.singletonList(field))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), parentId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN))
				;
		
		 List<ReadingContext> readings = selectRecordBuilder.get();
		 
		
		UpdateRecordBuilder<ReadingContext> updateRecordBuilder = null;
		
		if(readings != null && !readings.isEmpty()) {
			for(ReadingContext reading :readings) {
				Object value = reading.getDatum(field.getName());
				value = UnitsUtil.convertToSiUnit(value, from);
				
				reading.setDatum(field.getName(), value);
				
				updateRecordBuilder = new UpdateRecordBuilder<ReadingContext>()
						.table(module.getTableName())
						.module(module)
						.fields(allFields)
						;
				
				updateRecordBuilder.andCustomWhere("ID = ?", reading.getId())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), parentId+"", NumberOperators.EQUALS));
				updateRecordBuilder.update(reading);
			}
		}
		
		return SUCCESS;
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
