package com.facilio.workflows.context;

import java.io.Serializable;

public interface WorkflowExpression extends Serializable {
	
	public Object execute() throws Exception;
	
	public int getWorkflowExpressionType();
	
	public enum WorkflowExpressionType {
		EXPRESSION,
		ITERATION,
		CONDITION
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static WorkflowExpressionType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
