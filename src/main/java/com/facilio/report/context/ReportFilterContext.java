package com.facilio.report.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.Operator;

public class ReportFilterContext extends ReportFieldContext {

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
