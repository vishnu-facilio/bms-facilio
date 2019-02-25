package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.collections.UniqueMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Operator<E> {
	public int getOperatorId();
	
	public String getOperator();
	
	public String getWhereClause(String columnName, E value);
	
	@JsonIgnore
	public FacilioModulePredicate getPredicate(String fieldName, E value);
	
	public boolean isDynamicOperator();
	
	public boolean isValueNeeded();
	
	public List<Object> computeValues(E value);
	
	@SuppressWarnings("rawtypes")
	static final Map<Integer, Operator> OPERATOR_MAP = Collections.unmodifiableMap(initOperatorMap());
	@SuppressWarnings("rawtypes")
	static Map<Integer, Operator> initOperatorMap() {
		Map<Integer, Operator> operatorMap = new UniqueMap<>();
		
		for(Operator operator : CommonOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : StringOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : NumberOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : BooleanOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : DateOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : LookupOperator.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : PickListOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : BuildingOperator.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		for(Operator operator : EnumOperators.values()) {
			operatorMap.put(operator.getOperatorId(), operator);
		}
		
		return operatorMap;
		//Max operator code is 72
	}
}
