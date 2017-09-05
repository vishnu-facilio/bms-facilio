package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

public class Criteria {
	private long criteriaId;

	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private Map<Integer, Condition> conditions = null;
	public Map<Integer, Condition> getConditions() {
		return conditions;
	}
	public void setConditions(Map<Integer, Condition> conditions) {
		this.conditions = conditions;
		whereClause = null;
		predicate = null;
	}

	private String pattern = null;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
		whereClause = null;
		predicate = null;
	}

	private static final Pattern REG_EX = Pattern.compile("([1-9]\\d*)");
	private String whereClause;
	public String computeWhereClause() {
		if(whereClause == null || whereClause.isEmpty()) {
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
			whereClause =  builder.toString();
		}
		return whereClause;
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
	
	private static final Pattern SPLIT_REG_EX = Pattern.compile("([1-9]\\d*)|(\\()|(\\))|(and)|(or)", Pattern.CASE_INSENSITIVE);
	private Predicate predicate = null;
	public Predicate computePredicate() {
		if(predicate == null) {
			Stack<Predicate> operands = new Stack<>();
			Stack<String> operators = new Stack<>();
			Matcher matcher = SPLIT_REG_EX.matcher(pattern);
			while (matcher.find()) {
				if(matcher.group(1) != null) {
					Condition condition = conditions.get(Integer.parseInt(matcher.group(1)));
					if (condition == null) {
						throw new IllegalArgumentException("Pattern and conditions don't match");
					}
					operands.add(condition.getPredicate());
				}
				else if(matcher.group(2) != null) {
					operators.add(matcher.group());
				}
				else if(matcher.group(3) != null) {
					while (!operators.isEmpty() && !operators.peek().equals("(")) {
						operands.push(applyOp(operators.pop(), operands.pop(), operands.pop()));
					}
					operators.pop();
				}
				else if(matcher.group(4) != null || matcher.group(5) != null) {
					while (!operators.isEmpty() && !operators.peek().equals("(")) {
						operands.push(applyOp(operators.pop(), operands.pop(), operands.pop()));
					}
					operators.add(matcher.group());
				}
			}
			
			while (!operators.isEmpty()) {
				operands.push(applyOp(operators.pop(), operands.pop(), operands.pop()));
			}
			
			if(!operands.isEmpty()) {
				predicate = operands.pop();
			}
		}
		return predicate;
	}
	
	private Predicate applyOp (String operator, Predicate rightOperand, Predicate leftOperand) {
		if(operator.equalsIgnoreCase("and")) {
			return PredicateUtils.andPredicate(leftOperand, rightOperand);
		}
		else if(operator.equalsIgnoreCase("or")) {
			return PredicateUtils.orPredicate(leftOperand, rightOperand);
		}
		return null;
	}
	
	public void addAndCondition(Condition condition) {
		if(pattern != null && !pattern.isEmpty()) {
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
		else {
			conditions = new HashMap<>();
			pattern = "1";
			condition.setSequence(1);
			
			conditions = new HashMap<>();
			conditions.put(1, condition);
		}
	}
}
