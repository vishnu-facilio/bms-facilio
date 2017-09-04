package com.facilio.bmsconsole.criteria;

import java.util.List;

import org.apache.commons.collections.Predicate;

import com.facilio.bmsconsole.modules.FacilioField;

public interface Operator<E> {
	public String getOperator();
	
	public String getWhereClause(FacilioField field, E value);
	
	public Predicate getPredicate(FacilioField field, E value);
	
	public String getDynamicParameter();
	
	public List<Object> computeValues(E value);
	
}
