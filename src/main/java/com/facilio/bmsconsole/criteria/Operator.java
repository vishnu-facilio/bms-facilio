package com.facilio.bmsconsole.criteria;

import java.util.List;

import org.apache.commons.beanutils.BeanPredicate;

import com.facilio.bmsconsole.modules.FacilioField;

public interface Operator<E> {
	public String getOperator();
	
	public String getWhereClause(FacilioField field, E value);
	
	public FacilioModulePredicate getPredicate(FacilioField field, E value);
	
	public String getDynamicParameter();
	
	public List<Object> computeValues(E value);
	
}
