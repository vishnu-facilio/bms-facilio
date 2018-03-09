package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.bmsconsole.context.FormulaContext;
import com.facilio.util.ExpressionEvaluator;

public class Criteria extends ExpressionEvaluator<Predicate> {
	
	public Criteria() {
		// TODO Auto-generated constructor stub
		super.setRegEx(SPLIT_REG_EX);
	}
	
	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private long orgId = -1;
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
		if(conditions != null && !conditions.isEmpty()) {
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
			return  builder.toString();
		}
		return null;
	}
	
	public List<Object> getComputedValues() {
		List<Object> list = new ArrayList<>();
		if(conditions != null && !conditions.isEmpty()) {
			for(Condition condition : conditions.values()) {
				List<Object> computedValues = condition.getComputedValues();
				if(computedValues != null && computedValues.size() > 0) {
					for(Object val : computedValues) {
						list.add(val);
					}
				}
			}
		}
		return list;
	}
	
	private static final Pattern SPLIT_REG_EX = Pattern.compile("([1-9]\\d*)|(\\()|(\\))|(and)|(or)", Pattern.CASE_INSENSITIVE);
	public Predicate computePredicate() {
		return computePredicate(null);
	}
	
	private Map<String, Object> variables = null;
	public Predicate computePredicate(Map<String, Object> variables) {
		this.variables = variables;
		return evaluateExpression(pattern);
	}
	
	@Override
	public Predicate getOperand(String operand) {
		// TODO Auto-generated method stub
		Condition condition = conditions.get(Integer.parseInt(operand));
		if(condition != null) {
			return condition.computePredicate(variables);
		}
		return null;
	}
	
	@Override
	public Predicate applyOp (String operator, Predicate rightOperand, Predicate leftOperand) {
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
						.append(")")
						.append(" and ")
						.append(sequence)
						;
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
	
	public void addOrCondition(Condition condition) {
		if(pattern != null && !pattern.isEmpty()) {
			int sequence = conditions.size()+1;
			condition.setSequence(sequence);
			conditions.put(sequence, condition);
			
			StringBuilder newPattern = new StringBuilder();
			newPattern.append("(")
						.append(pattern)
						.append(")")
						.append(" or ")
						.append(sequence)
						;
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
	
	public void addAndConditions(List<Condition> newConditions) {
		if(newConditions != null && !newConditions.isEmpty()) {
			int sequence = 1;
			StringBuilder newPattern = new StringBuilder();
			if(pattern != null && !pattern.isEmpty()) {
				sequence = conditions.size() + 1;
				newPattern.append("(")
							.append(pattern)
							.append(")")
							.append(" and ");
			}
			else {
				conditions = new HashMap<>();
			}
			
			boolean isFirst = true;
			for(Condition condition : newConditions) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					newPattern.append(" and ");
				}
				newPattern.append(sequence);
				condition.setSequence(sequence);
				conditions.put(sequence++, condition);
			}
			setPattern(newPattern.toString());
		}
	}
	
	public void groupOrConditions(List<Condition> newConditions) {
		if(newConditions != null && !newConditions.isEmpty()) {
			int sequence = 1;
			StringBuilder newPattern = new StringBuilder();
			if(pattern != null && !pattern.isEmpty()) {
				sequence = conditions.size() + 1;
				newPattern.append(pattern).append(" and ");
			}
			else {
				conditions = new HashMap<>();
			}
			
			boolean isFirst = true;
			for(Condition condition : newConditions) {
				if(isFirst) {
					isFirst = false;
					newPattern.append("(");
				}
				else {
					newPattern.append(" or ");
				}
				newPattern.append(sequence);
				condition.setSequence(sequence);
				conditions.put(sequence++, condition);
			}
			newPattern.append(")");
			setPattern(newPattern.toString());
		}
	}
	
	public void andCriteria(Criteria newCriteria) {
		Map<Integer, Condition> newConditions = newCriteria.getConditions();
		String newPattern = newCriteria.getPattern();
		int sequence = 1;
		StringBuilder finalPattern = new StringBuilder();
		if(newConditions != null && !newConditions.isEmpty()) {
			if(pattern != null && !pattern.isEmpty()) {
				sequence = conditions.size() + 1;
				finalPattern.append("(")
							.append(pattern)
							.append(")")
							.append(" and ");
			}
			else {
				conditions = new HashMap<>();
			}
			Matcher matcher = REG_EX.matcher(newPattern);
			int i = 0;
			while (matcher.find()) {
				Condition condition = newConditions.get(Integer.parseInt(matcher.group(1)));
				finalPattern.append(newPattern.substring(i, matcher.start()));
				condition.setSequence(sequence);
				conditions.put(sequence, condition);
				finalPattern.append(sequence++);
				i = matcher.end();
			}
			finalPattern.append(newPattern.substring(i, newPattern.length()));
			setPattern(finalPattern.toString());
		}
	}
	
}
