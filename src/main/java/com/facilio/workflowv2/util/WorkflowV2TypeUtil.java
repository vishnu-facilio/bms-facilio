package com.facilio.workflowv2.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.facilio.scriptengine.context.Value;
import com.facilio.util.FacilioUtil;

public class WorkflowV2TypeUtil {
	
	public static Double assignNumericValuesForComparison(Value value) {
		
		Double numericalValue = null;
		if(value.asObject() == null || (value.asObject() instanceof String && StringUtils.isEmpty(value.asString()))) {
			numericalValue = 0d;
    	}
		else if(value.asObject() instanceof String && NumberUtils.isNumber(value.asString())) {
			numericalValue = value.asDouble();
    	}
		else if(value.asObject() instanceof Number && !value.asDouble().isNaN()) {
			numericalValue = value.asDouble();
		}
    	else if(value.asObject() instanceof Boolean) {
    		numericalValue = (value.asBoolean().booleanValue() == true) ? 1d : 0d;
    	}
    	else if(value.asObject() instanceof ArrayList && ((ArrayList)value.asObject()).isEmpty()) {
    		numericalValue = 0d;
    	}
		return numericalValue;
	}
	
	public static boolean evaluateEquality(Value left, Value right) {
        
        boolean equalityResult = false;
		
		if(left.asObject() != null && right.asObject() != null && !left.asObject().getClass().equals(right.asObject().getClass())) 
		{
			Double leftNumericalValue = assignNumericValuesForComparison(left);
			Double rightNumericalValue = assignNumericValuesForComparison(right);
			equalityResult = (leftNumericalValue != null && rightNumericalValue != null) ? leftNumericalValue.equals(rightNumericalValue) : left.equals(right);				
		}
		else 
		{
			if(left.asObject() != null && right.asObject() != null && FacilioUtil.isNumeric(left.asString()) && FacilioUtil.isNumeric(right.asString()) ) {
				equalityResult =  left.asDouble().equals(right.asDouble());
	    	}
			else if(left.asObject() != null && left.asObject() instanceof ArrayList && ((ArrayList)left.asObject()).isEmpty() &&
					right.asObject() != null && right.asObject() instanceof ArrayList && ((ArrayList)right.asObject()).isEmpty()) {
				equalityResult = false;
	    	}
			else if(left.asObject() != null && left.asObject() instanceof HashMap && ((HashMap)left.asObject()).isEmpty() &&
					right.asObject() != null && right.asObject() instanceof HashMap && ((HashMap)right.asObject()).isEmpty()) {
				equalityResult = false;
	    	}
			else {
				equalityResult =  left.equals(right);
			}
		}
		return equalityResult;	
    }
	
}
