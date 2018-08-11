package com.facilio.workflows.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;

public enum ExpressionAggregateOperator implements ExpressionAggregateInterface {
	
	FIRST_VALUE(0,"[0]","{$place_holder$}") {
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			return props.get(0).get(fieldName);
		}
	},
	COUNT(1,"count","count({$place_holder$})") {
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			return props.size();
		}
	},
	AVERAGE(2,"avg","avg({$place_holder$})") {
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			double sum = 0;
			for(Map<String, Object> prop:props) {
				if(prop.get(fieldName) != null) {
					sum = sum + (double) prop.get(fieldName);
				}
			}
			
			return sum/props.size();
		}
	},
	SUM(3,"sum","sum({$place_holder$})"){
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			double sum = 0;
			for(Map<String, Object> prop:props) {
				if(prop.get(fieldName) != null) {
					sum = sum + (double) prop.get(fieldName);
				}
			}
			return sum;
		}
	},
	MIN(4,"min","min({$place_holder$})"){
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			double min = 0;
			boolean isFirst = true;
			for(Map<String, Object> prop:props) {
				if(prop.get(fieldName) != null) {					
					double value = (double) prop.get(fieldName);
					if(isFirst) {
						isFirst = false;
						min = value;
					}
					min = value < min ? value:min;
				}
			}
			return min;
		}
	},
	MAX(5,"max","max({$place_holder$})"){
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			double max = 0;
			boolean isFirst = true;
			for(Map<String, Object> prop:props) {
				if(prop.get(fieldName) != null) {					
					double value = (double) prop.get(fieldName);
					if(isFirst) {
						isFirst = false;
						max = value;
					}
					max = value > max ? value:max;
				}
			}
			return max;
		}
	},
	LAST_VALUE(6,"lastValue","{$place_holder$}") {
		public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
			return props.get(props.size()-1).get(fieldName);
		}
	},
	COUNT_RUNNING_TIME(7,"countRunningTime","{$place_holder$}")
	;
	
	private Integer value;
	private String name;
	private String stringValue;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}
	public String getStringValue() {
		return stringValue;
	}
	ExpressionAggregateOperator(Integer value,String name,String stringValue) {
		this.value = value;
		this.name= name;
		this.stringValue = stringValue;
	}
	public static ExpressionAggregateOperator getExpressionAggregateOperator(String name) {
		return EXP_AGGREGATE_OPERATOR_MAP_BY_NAME.get(name);
	}
	static final Map<String, ExpressionAggregateOperator> EXP_AGGREGATE_OPERATOR_MAP_BY_NAME = Collections.unmodifiableMap(initTypeMap());
	static Map<String, ExpressionAggregateOperator> initTypeMap() {
		Map<String, ExpressionAggregateOperator> typeMap = new HashMap<>();
		for(ExpressionAggregateOperator type : ExpressionAggregateOperator.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
	public FacilioField getSelectField(FacilioField field) throws Exception {
		String selectFieldString =stringValue.replace("{$place_holder$}", field.getColumnName());
		field.setColumnName(selectFieldString);
		field.setDataType(FieldType.NUMBER);
		return field;
	}
	@Override
	public Object getAggregateResult(List<Map<String, Object>> props,String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}
}