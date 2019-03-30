package com.facilio.sql;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WhereBuilder implements WhereBuilderIfc<WhereBuilder>{
	private static final Logger LOGGER = LogManager.getLogger(WhereBuilder.class.getName());
	
	private StringBuilder condition = new StringBuilder();
	private List<Object> values = new ArrayList<>();;
	
	public WhereBuilder() {
		
	}
	
	public WhereBuilder(WhereBuilder whereBuilder) {
		// TODO Auto-generated constructor stub
		this.condition = new StringBuilder(whereBuilder.condition);
		if (whereBuilder.values != null) {
			this.values = new ArrayList<>(whereBuilder.values);
		}
	}
	
	@Override
	public WhereBuilder andCustomWhere(String where, Object... values) {
		return customWhere(true, where, values);
	}
	
	@Override
	public WhereBuilder orCustomWhere(String where, Object... values) {
		return customWhere(false, where, values);
	}
	
	private WhereBuilder customWhere(boolean isAND, String where, Object... values) {
		if(where != null && !where.isEmpty()) {
			int count = StringUtils.countMatches(where, "?");
			if(values != null && values.length < count) {
				throw new IllegalArgumentException("No. of where values doesn't match the number of ?");
			}
			
			checkIfFirstAndAdd(isAND);
			condition.append("(")
					.append(where)
					.append(")");
			if(values != null) {
				for(Object val : values) {
					if (val instanceof Enum) {
						printTrace("Enum is give as value in custom where. This is wrong");
					}
					
					this.values.add(val);
				}
			}
		}
		return this;
	}
	
	@Override
	public WhereBuilder andCondition(Condition condition) {
		return condition(true, condition);
	}
	
	@Override
	public WhereBuilder orCondition(Condition condition) {
		return condition(false, condition);
	}
	
	private WhereBuilder condition(boolean isAND, Condition condition) {
		
		if (condition == null) { 
//			throw new IllegalArgumentException("Condition cannot be null");
			printTrace("Condition cannot be null. This is wrong");
		}
		
		String computeAndGetWhereClause = condition.computeAndGetWhereClause();
		if (computeAndGetWhereClause == null || computeAndGetWhereClause.isEmpty()) { //Checking for 75 alone
//			throw new IllegalArgumentException("Condition cannot be null");
			printTrace("Condition where class cannot be null. This is wrong\nField Name : "+condition.getFieldName()+", Column Name : "+condition.getColumnName());
		}
		
		checkIfFirstAndAdd(isAND);
		this.condition.append(computeAndGetWhereClause);
		if(condition.getComputedValues() != null) {
			values.addAll(condition.getComputedValues());
		}
		return this;
	}
	
	@Override
	public WhereBuilder andCriteria(Criteria criteria) {
		return criteria(true, criteria);
	}
	
	@Override
	public WhereBuilder orCriteria(Criteria criteria) {
		return criteria(false, criteria);
	}
	
	private static final int MAX_LINES_TO_BE_PRINTED = 30;
	private void printTrace (String msg) {
		StringBuilder builder = new StringBuilder(msg)
									.append("\nTruncated Trace\n");
		
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < Math.min(MAX_LINES_TO_BE_PRINTED, trace.length); i++) {
			builder.append(trace[i])
					.append("\n");
		}
		LOGGER.info(builder.toString());
	}
	
	private WhereBuilder criteria(boolean isAND, Criteria criteria) {
		
		if (criteria == null || criteria.isEmpty()) { 
			printTrace("Criteria cannot be null. This is wrong");
		}
		
		if (criteria != null) {
			checkIfFirstAndAdd(isAND);
			this.condition.append("(")
						.append(criteria.computeWhereClause())
						.append(")");
			if(criteria.getComputedValues() != null) {
				values.addAll(criteria.getComputedValues());
			}
		}
		return this;
	}
	
	public Object[] getValues() {
		return values.toArray();
	}
	public String getWhereClause() {
		return condition.toString();
	}
	
	private void checkIfFirstAndAdd(boolean isAND) {
		if(condition.length() != 0) {
			if(isAND) {
				condition.append(" AND ");
			}
			else {
				condition.append(" OR ");
			}
		}
	}
	
	public boolean isEmpty() {
		return condition.length() == 0;
	}
}
