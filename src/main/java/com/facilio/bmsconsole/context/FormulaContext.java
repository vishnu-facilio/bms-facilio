package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fw.BeanFactory;

public class FormulaContext extends ModuleBaseWithCustomFields {

	private Long selectFieldId;
	private int aggregateOperationValue;
	private Long criteriaId;
	
	public AggregateOperator getAggregateOperator() {
		return AggregateOperator.getAggregateOperator(getAggregateOperationValue());
	}
	public Long getSelectFieldId() {
		return selectFieldId;
	}
	public void setSelectFieldId(Long selectFieldId) {
		this.selectFieldId = selectFieldId;
	}
	public int getAggregateOperationValue() {
		return aggregateOperationValue;
	}
	public void setAggregateOperationValue(int aggregateOperationValue) {
		this.aggregateOperationValue = aggregateOperationValue;
	}
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	public enum AggregateOperator {
		
		COUNT(1,"count"),
		AVERAGE(2,"avg"),
		SUM(3,"sum")
		;
		
		private int value;
		private String stringValue;
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getStringValue() {
			return stringValue;
		}
		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}
		AggregateOperator(int value,String stringValue) {
			this.value = value;
			this.stringValue = stringValue;
		}
		public static AggregateOperator getAggregateOperator(int value) {
			return AGGREGATE_OPERATOR_MAP.get(value);
		}
		public FacilioField getSelectField(FacilioField field) throws Exception {
			String selectFieldString = this.getStringValue() + "(" +field.getColumnName()+ ")";
			
			FacilioField selectField = new FacilioField();
			selectField.setName("value");
			selectField.setColumnName(selectFieldString);
			
			return selectField;
		}
		private static final Map<Integer, AggregateOperator> AGGREGATE_OPERATOR_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, AggregateOperator> initTypeMap() {
			Map<Integer, AggregateOperator> typeMap = new HashMap<>();
			for(AggregateOperator type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
}

