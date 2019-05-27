package com.facilio.db.criteria.operators;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.google.common.base.Objects;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public enum FieldOperator implements Operator<Object> {

	EQUAL(74, " = "),
	NOT_EQUAL(75, " != "),
	LESS_THAN(76, " < "),
	LESS_THAN_EQUAL(77, " <= "),
	GREATER_THAN(78, " > "),
	GREATER_THAN_EQUAL(79, " >= "),
	;
	
	private static Logger log = LogManager.getLogger(FieldOperator.class.getName());

	private FieldOperator(int operatorId, String operator) {
		this.operatorId = operatorId;
		this.operator = operator;
	}
	
	private int operatorId;
	@Override
	public int getOperatorId() {
		return operatorId;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	@Override
	public String getWhereClause(String fieldName, Object valueFieldName) {
		try {
			if(StringUtils.isNotEmpty(fieldName) && valueFieldName != null && StringUtils.isNotEmpty((String) valueFieldName)) {
				String[] fieldData = fieldName.split("\\.");
				String[] valueData = valueFieldName.toString().split("\\.");
				if(fieldData.length > 1 && valueData.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					
					FacilioField field = modBean.getField(fieldData[1], fieldData[0]);
					FacilioField value = modBean.getField(valueData[1], valueData[0]);
					
					if (field != null && value != null) {
						StringBuilder builder = new StringBuilder();
						builder.append(field.getCompleteColumnName());
						builder.append(getOperator());
						builder.append(value.getCompleteColumnName());
						return builder.toString();
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}

	@Override
	public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
		FacilioModulePredicate predicate = new FacilioModulePredicate(fieldName, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
						&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
					return true;
				}
				return Objects.equal(object, valueField);
			}
		});
		return predicate;
	}

	@Override
	public boolean isPlaceHoldersMandatory() {
		return true;
	}

	@Override
	public boolean isDynamicOperator() {
		return false;
	}

	@Override
	public boolean isValueNeeded() {
		return false;
	}

	@Override
	public List<Object> computeValues(Object value) {
		return null;
	}

}
