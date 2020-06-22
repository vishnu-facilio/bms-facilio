package com.facilio.workflowv2.contexts;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;

public class DBParamContext {
	
	@Deprecated
	String moduleName;		// dont use this anywhere
	
	String fieldName;
	String aggregateString;
	String aggregateFieldName;
	Pair<Integer, Integer> range;
	String sortByFieldName;
	String sortOrder;
	Criteria criteria;
	Criteria fieldCriteria;
	boolean skipUnitConversion;

	public boolean isSkipUnitConversion() {
		return skipUnitConversion;
	}
	public void setSkipUnitConversion(boolean skipUnitConversion) {
		this.skipUnitConversion = skipUnitConversion;
	}

	int limit;
	boolean ignoreMarkedReadings;
	String groupBy;
	
	int seqOrder = 0;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	
	public boolean isIgnoreMarkedReadings() {
		return ignoreMarkedReadings;
	}
	public void setIgnoreMarkedReadings(boolean ignoreMarkedReadings) {
		this.ignoreMarkedReadings = ignoreMarkedReadings;
	}
	
	public AggregateOperator getAggregateOpperator() {
		return AggregateOperator.getAggregateOperator(getAggregateString());
	}
	
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public int getSeqOrder() {
		return seqOrder;
	}
	public void setSeqOrder(int seqOrder) {
		this.seqOrder = seqOrder;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getAggregateString() {
		return aggregateString;
	}
	public void setAggregateString(String aggregateString) {
		this.aggregateString = aggregateString;
	}
	public Pair<Integer, Integer> getRange() {
		return range;
	}
	public void setRange(Pair<Integer, Integer> range) {
		this.range = range;
	}
	public String getSortByFieldName() {
		return sortByFieldName;
	}
	public void setSortByFieldName(String sortByFieldName) {
		this.sortByFieldName = sortByFieldName;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public String getAggregateFieldName() {
		return aggregateFieldName;
	}
	public void setAggregateFieldName(String aggregateFieldName) {
		this.aggregateFieldName = aggregateFieldName;
	}
	
	public Criteria getFieldCriteria() {
		return fieldCriteria;
	}
	public void setFieldCriteria(Criteria fieldCriteria) {
		this.fieldCriteria = fieldCriteria;
	}

	@Override
	public String toString() {
		return "DBParamContext [fieldName=" + fieldName + ", aggregateString=" + aggregateString + ", range=" + range
				+ ", sortByFieldName=" + sortByFieldName + ", sortOrder=" + sortOrder + ", criteria=" + criteria
				+ ", seqOrder=" + seqOrder + "]";
	}
}
