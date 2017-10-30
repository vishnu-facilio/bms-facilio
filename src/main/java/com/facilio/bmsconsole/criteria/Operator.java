package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Operator<E> {
	public int getOperatorId();
	
	public String getOperator();
	
	public String getWhereClause(String columnName, E value);
	
	public FacilioModulePredicate getPredicate(String fieldName, E value);
	
	public String getDynamicParameter();
	
	public List<Object> computeValues(E value);
	
	@SuppressWarnings("rawtypes")
	static final Map<Integer, Operator> OPERATOR_MAP = Collections.unmodifiableMap(initOperatorMap());
	@SuppressWarnings("rawtypes")
	static Map<Integer, Operator> initOperatorMap() {
		Map<Integer, Operator> operatorMap = new UniqueOperatorMap();
		
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
		return operatorMap;
	}
	
	@SuppressWarnings("rawtypes")
	static class UniqueOperatorMap extends HashMap<Integer, Operator> {
		@Override
		public Operator put(Integer key, Operator value) {
			// TODO Auto-generated method stub
			if(containsKey(key)) {
				throw new IllegalArgumentException("Duplicate ID IN Operators");
			}
			return super.put(key, value);
		}
	}
}
