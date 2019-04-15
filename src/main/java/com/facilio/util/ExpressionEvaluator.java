package com.facilio.util;

import org.apache.struts2.json.annotations.JSON;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExpressionEvaluator<E> {
	
	private Pattern regEx;
	@JSON(serialize=false)
	public Pattern getRegEx() {
		return regEx;
	}
	public void setRegEx(Pattern regEx) {
		this.regEx = regEx;
	}

	public E evaluateExpression(String pattern) {
		Stack<E> operands = new Stack<>();
		Stack<String> operators = new Stack<>();
		Matcher matcher = regEx.matcher(pattern);
		while (matcher.find()) {
			if(matcher.group(1) != null) {
				E operand = getOperand(matcher.group(1));
				if (operand == null) {
					throw new IllegalArgumentException("Invalid operand value");
				}
				operands.add(operand);
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
			return operands.pop();
		}
		return null;
	}
	
	public abstract E getOperand(String operand);
	
	public abstract E applyOp (String operator, E rightOperand, E leftOperand);
}
