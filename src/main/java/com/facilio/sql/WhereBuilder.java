package com.facilio.sql;

import org.apache.commons.lang3.StringUtils;

class WhereBuilder {
	private String condition;
	private Object[] values;
	
	public WhereBuilder where(String where, Object... values) {
		
		int count = StringUtils.countMatches(where, "?");
		if(values.length < count) {
			throw new IllegalArgumentException("No. of where values doesn't match the number of ?");
		}
		
		this.condition = where;
		this.values = values;
		return this;
	}
	
	public Object[] getValues() {
		return values;
	}
	public String getCondition() {
		return condition;
	}
	
	public void checkForNull() {
		if(condition == null || condition.isEmpty()) {
			throw new IllegalArgumentException("No where condition specified");
		}
	}
}
