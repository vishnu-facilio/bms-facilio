package com.facilio.bmsconsole.criteria;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class FacilioModulePredicate extends BeanPredicate {

	private static Logger log = LogManager.getLogger(FacilioModulePredicate.class.getName());

	public FacilioModulePredicate(String propertyName, Predicate predicate) {
		super(propertyName, predicate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean evaluate(Object object) {
		// TODO Auto-generated method stub
		if(object instanceof Map<?,?>) {
			Object propertyVal = ((Map<String, Object>) object).get(getPropertyName());
			return getPredicate().evaluate(propertyVal);
		}
		else if(PropertyUtils.isReadable(object, getPropertyName())) {
			return super.evaluate(object);
		}
		else if(object instanceof ModuleBaseWithCustomFields) {
			Object propertyVal = ((ModuleBaseWithCustomFields) object).getDatum(getPropertyName());
			return getPredicate().evaluate(propertyVal);
		}
		else {
			try {
				Map<String, Object> props = FieldUtil.getAsProperties(object);
				Object propertyVal = props.get(getPropertyName());
				return getPredicate().evaluate(propertyVal);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
		}
		return false;
	}
}
