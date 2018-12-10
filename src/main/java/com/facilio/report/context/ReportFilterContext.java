package com.facilio.report.context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioField;

public class ReportFilterContext {

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private String filterFieldName;
	public String getFilterFieldName() {
		return filterFieldName;
	}
	public void setFilterFieldName(String filterFieldName) {
		this.filterFieldName = filterFieldName;
	}
	
	private Operator filterOperator;
	public Operator getFilterOperatorEnum() {
		return filterOperator;
	}
	public void setFilterOperator(Operator filterOperator) {
		this.filterOperator = filterOperator;
	}
	public int getFilterOperator() {
		if (filterOperator != null) {
			return filterOperator.getOperatorId();
		}
		return -1;
	}
	public void setFilterOperator(int filterOperator) {
		if (filterOperator > 0) {
			this.filterOperator = Operator.OPERATOR_MAP.get(filterOperator);
		}
	}
	
	private String filterValue;
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	private FacilioField field;
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
		
		this.fieldId = field.getId();
		this.fieldName = field.getName();
		
		if (field.getModule() != null) {
			this.moduleName = field.getModule().getName();
		}
	}

	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private Boolean dataFilter;
	public Boolean getDataFilter() {
		return dataFilter;
	}
	public void setDataFilter(Boolean dataFilter) {
		this.dataFilter = dataFilter;
	}
	public boolean isDataFilter() {
		if (dataFilter != null) {
			return dataFilter.booleanValue();
		}
		return false;
	}
}
