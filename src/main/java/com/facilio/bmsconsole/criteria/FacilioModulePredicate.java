package com.facilio.bmsconsole.criteria;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class FacilioModulePredicate extends BeanPredicate {

	public FacilioModulePredicate(String propertyName, Predicate predicate) {
		super(propertyName, predicate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean evaluate(Object object) {
		// TODO Auto-generated method stub
		if(PropertyUtils.isReadable(object, getPropertyName())) {
			return super.evaluate(object);
		}
		else if(object instanceof ModuleBaseWithCustomFields) {
			Object propertyVal = ((ModuleBaseWithCustomFields) object).getCustomProp(getPropertyName());
			return getPredicate().evaluate(propertyVal);
		}
		return false;
	}
}
