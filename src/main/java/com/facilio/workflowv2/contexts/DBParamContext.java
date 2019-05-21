package com.facilio.workflowv2.contexts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import org.apache.commons.lang3.tuple.Pair;

public class DBParamContext {
	
	String fieldName;
	String aggregateString;
	Pair<Integer, Integer> range;
	String sortByFieldName;
	String sortOrder;
	Criteria criteria;
	int limit;
	boolean ignoreMarkedReadings;
	String groupBy;
	
	int seqOrder = 0;
	
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

	@Override
	public String toString() {
		return "DBParamContext [fieldName=" + fieldName + ", aggregateString=" + aggregateString + ", range=" + range
				+ ", sortByFieldName=" + sortByFieldName + ", sortOrder=" + sortOrder + ", criteria=" + criteria
				+ ", seqOrder=" + seqOrder + "]";
	}
	
	Map<String, List<Map<String, Object>>> cache;

	public Map<String, List<Map<String, Object>>> getCache() {
		return cache;
	}
	public void setCache(Map<String, List<Map<String, Object>>> cache) {
		this.cache = cache;
	}
}
