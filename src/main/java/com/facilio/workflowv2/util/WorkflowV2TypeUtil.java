package com.facilio.workflowv2.util;

import org.apache.commons.lang.StringUtils;

import com.facilio.util.FacilioUtil;
import com.facilio.workflowv2.contexts.Value;

public class WorkflowV2TypeUtil {
		
	public static double assignNumericValuesForComparison(Value value) {
		
		Double numericalValue = null;
		if(value.asObject() == null || (value.asObject() instanceof String && StringUtils.isEmpty(value.asString()))) {
			numericalValue = 0d;
    	}
		else if(value.asObject() instanceof Number) {
			numericalValue = value.asDouble();
		}
    	else if(value.asObject() instanceof Boolean) {
    		numericalValue = (value.asBoolean() == true) ? 1d : 0d;
    	}
		return numericalValue;
	}
	
	public static void assignBothNumericValuesForComparison(Value left, Double numericalLeftValue, Value right, Double numericalRightValue)
	{	
		numericalLeftValue = WorkflowV2TypeUtil.assignNumericValuesForComparison(left);
		numericalRightValue = WorkflowV2TypeUtil.assignNumericValuesForComparison(right);	
	}
	
	public static boolean evaluateEquality(Value left, Value right) {	
		if(left.asObject() != null && right.asObject() != null && FacilioUtil.isNumeric(left.asString()) && FacilioUtil.isNumeric(right.asString()) ) {
    		return (left.asDouble().equals(right.asDouble()));
    	}
    	return (left.equals(right));	
    }
	
}
