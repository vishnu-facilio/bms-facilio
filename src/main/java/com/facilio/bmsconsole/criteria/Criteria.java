package com.facilio.bmsconsole.criteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.util.ExpressionEvaluator;
import com.google.common.base.Objects;

public class Criteria extends ExpressionEvaluator<Predicate> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	private Map<String, Condition> conditions = null;
	public Map<String, Condition> getConditions() {
		return conditions;
	}
	public void setConditions(Map<String, Condition> conditions) {
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
				Condition condition = conditions.get(matcher.group(1));
				if (condition == null) {
					throw new IllegalArgumentException("Pattern and conditions don't match");
				}
				String computedCondition = condition.computeAndGetWhereClause();
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
	
	public void validatePattern() {
		String[] tokens = this.tokenize();
		postfix(tokens);
	}
	
	public String postfix(String[] tokens) {
		Stack<String> stack = new Stack<>();
		List<String> outPut = new ArrayList<>();
		String prev = null;
		for (String e: tokens) {
			boolean pass = isNumber(e) || e.equalsIgnoreCase("and") || e.equalsIgnoreCase("or") || e.equals(")") || e.equals("(");
			if (!pass) {
				throw new IllegalArgumentException("Invalid Character");
		    }
			if (e.equals("(") && prev != null && !prev.equals("(") && !prev.equalsIgnoreCase("and") && prev.equalsIgnoreCase("or")) {
		       throw new IllegalArgumentException("Invalid Expression");
		    }
	        else if (e.equals(")") && !prev.equals(")") && !isNumber(prev)) {
	        	throw new IllegalArgumentException("Invalid Expression");
	        }
		    else if ((e.equalsIgnoreCase("and") || e.equalsIgnoreCase("or")) && !prev.equals(")") && !isNumber(prev)) {
		    	throw new IllegalArgumentException("Invalid Expression");
		    }
		    else if (isNumber(e) && prev != null &&  !prev.equals("(") && !prev.equalsIgnoreCase("and") && !prev.equalsIgnoreCase("or")) {
		    	throw new IllegalArgumentException("Invalid Expression");
		    }
			
			if (e.equals("(")) {
				stack.push(e);
			} else if (isNumber(e)) {
				outPut.add(e);
			} else if (e.equalsIgnoreCase("and")) {
				while (!stack.isEmpty()) {
					String p = stack.peek();
					if (p.equals("(") || p.equalsIgnoreCase("or")) {
						break;
					}
					outPut.add(stack.pop());
				}
				stack.add(e);
			} else if (e.equalsIgnoreCase("or")) {
				while (!stack.isEmpty()) {
					String p = stack.peek();
					if (p.equals("(")) {
						break;
					}
					outPut.add(stack.pop());
				}
				stack.add(e);
			} else if (e.equals(")")) {
				while (!stack.isEmpty()) {
					String p = stack.pop();
					if (p.equals("(")) {
						break;
					}
					outPut.add(p);
				}
			}
			prev = e;  
		}
		if (!stack.isEmpty()) {
			throw new IllegalArgumentException("Invalid Expression");
		}
		return StringUtils.join(outPut.toArray(new String[outPut.size()]));
	}
	
	private String[] tokenize() {
		List<String> tokens = new ArrayList<>();
		String curr = "";
		for (int i = 0; i < this.pattern.length(); i++) {
			String c = Character.toString(this.pattern.charAt(i));
			if (c.equals("(")) {
				tokens.add(c);
			} else if (c.equals(")")) {
				if (!curr.isEmpty()) {
					tokens.add(curr);
					curr = "";
				}
				tokens.add(c);
			} else if (isNumber(c)) {
				if (curr.equalsIgnoreCase("and") || curr.equalsIgnoreCase("or") || curr.equalsIgnoreCase("(")) {
		            tokens.add(curr);
		            curr = "";
		        } else if (curr.isEmpty()) {
		            curr += c;
		        } else {
		        	if (isNumber(curr.charAt(curr.length() - 1))) {
		        		curr += c;
		        	} else {
		        		tokens.add(curr);
		        		curr = c;
		        	}
		        }
			} else if (isAlphabet(c)) {
				if (!curr.isEmpty() && isNumber(curr.charAt(curr.length() - 1))) {
		            tokens.add(curr);
		            curr = "";
		        }
				curr += c;
				if (curr.equalsIgnoreCase("and") || curr.equalsIgnoreCase("or")) {
					tokens.add(curr);
					curr = "";
				}
			} else if (isSpace(c)) {
				if (!curr.isEmpty()) {
		            tokens.add(curr);
		            curr = "";
		        }
			} else {
	          if (!curr.isEmpty()) {
		        tokens.add(curr);
		      }
		      curr = "";
		      tokens.add(c);
			}
		}
		if (!curr.isEmpty()) {
			tokens.add(curr);
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	private boolean isSpace(String c) {
		return StringUtils.isBlank(c);
	}
	private boolean isAlphabet(String c) {
		return StringUtils.isAlpha(c);
	}
	private boolean isNumber(char c) {
		return isNumber(Character.toString(c));
	}
	private boolean isNumber(String c) {
		return StringUtils.isNumeric(c);
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
		Condition condition = conditions.get(operand);
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
			conditions.put(String.valueOf(sequence), condition);
			
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
			conditions.put("1", condition);
		}
	}
	
	public void addOrCondition(Condition condition) {
		if(pattern != null && !pattern.isEmpty()) {
			int sequence = conditions.size()+1;
			condition.setSequence(sequence);
			conditions.put(String.valueOf(sequence), condition);
			
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
			conditions.put("1", condition);
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
				conditions.put(String.valueOf(sequence++), condition);
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
				conditions.put(String.valueOf(sequence++), condition);
			}
			newPattern.append(")");
			setPattern(newPattern.toString());
		}
	}
	
	public void andCriteria(Criteria newCriteria) {
		appendCriteria(newCriteria, " and ");
	}
	
	public void orCriteria(Criteria newCriteria) {
		appendCriteria(newCriteria, " or ");
	}
	
	private void appendCriteria(Criteria newCriteria, String operator) {
		if (newCriteria == null) {
			return;
		}
		Map<String, Condition> newConditions = newCriteria.getConditions();
		String newPattern = newCriteria.getPattern();
		int sequence = 1;
		StringBuilder finalPattern = new StringBuilder();
		if(newConditions != null && !newConditions.isEmpty()) {
			if(pattern != null && !pattern.isEmpty()) {
				sequence = conditions.size() + 1;
				finalPattern.append("(")
							.append(pattern)
							.append(")")
							.append(operator)
							.append("(");
				
				Matcher matcher = REG_EX.matcher(newPattern);
				int i = 0;
				while (matcher.find()) {
					Condition condition = newConditions.get(matcher.group(1));
					finalPattern.append(newPattern.substring(i, matcher.start()));
					condition.setSequence(sequence);
					conditions.put(String.valueOf(sequence), condition);
					finalPattern.append(sequence++);
					i = matcher.end();
				}
				finalPattern.append(newPattern.substring(i, newPattern.length()))
							.append(")");
				setPattern(finalPattern.toString());
			}
			else {
				pattern = newPattern;
				conditions = new HashMap<>(newConditions); 
			}
		}
	}
	
	public boolean isEmpty() {
		
		if(pattern==null ) {
			return true;
		}
		return pattern.isEmpty();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ID : "+criteriaId+"\nPattern : "+pattern+"\nConditions : "+conditions;
	}
	
	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
		if (this == other) {
            return true;
        }
		if (other != null && other instanceof Criteria ) {
			Criteria otherCriteria = (Criteria) other;
			if (Objects.equal(this.pattern, otherCriteria.pattern) &&
					Objects.equal(this.conditions, otherCriteria.conditions)
					) {
				return true;
			}
		}
		return false;
	}
}
