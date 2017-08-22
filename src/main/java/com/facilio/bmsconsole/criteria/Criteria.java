package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Criteria {
	private long criteriaId;

	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}

	private Map<Integer, Condition> conditions = null;

	public Map<Integer, Condition> getConditions() {
		return conditions;
	}

	public void setConditions(Map<Integer, Condition> conditions) {
		this.conditions = conditions;
	}

	private String pattern = null;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	private static final Pattern REG_EX = Pattern.compile("([1-9]\\d*)");
	public String computeWhereClause() {
		Matcher matcher = REG_EX.matcher(pattern);
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (matcher.find()) {
			Condition condition = conditions.get(Integer.parseInt(matcher.group(1)));
			if (condition == null) {
				throw new IllegalArgumentException("Pattern and conditions don't match");
			}
			String computedCondition = condition.getComputedWhereClause();
			builder.append(pattern.substring(i, matcher.start()));
			builder.append(computedCondition);
			i = matcher.end();
		}
		builder.append(pattern.substring(i, pattern.length()));
		return builder.toString();
	}
	
	public List<Object> getComputedValues() {
		List<Object> list = new ArrayList<>();
		for(Condition condition : conditions.values()) {
			List<Object> computedValues = condition.getComputedValues();
			if(computedValues != null && computedValues.size() > 0) {
				for(Object val : computedValues) {
					list.add(val);
				}
			}
		}
		return list;
	}
	
	public void addAndCondition(Condition condition) {
		int sequence = conditions.size()+1;
		condition.setSequence(sequence);
		conditions.put(sequence, condition);
		
		StringBuilder newPattern = new StringBuilder();
		newPattern.append("(")
					.append(pattern)
					.append(" && ")
					.append(sequence)
					.append(")");
		setPattern(newPattern.toString());
	}
}
