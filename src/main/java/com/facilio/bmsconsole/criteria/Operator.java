package com.facilio.bmsconsole.criteria;

import java.util.List;

public interface Operator {
	public String getOperator();
	
	public String getWhereClause(String columnName, String value);
	
	public String getDynamicParameter();
	
	public List<Object> computeValues(String value);
	
}
