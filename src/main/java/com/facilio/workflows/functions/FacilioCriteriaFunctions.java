package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.json.simple.JSONObject;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioCriteriaFunctions implements FacilioWorkflowFunctionInterface {

	
	GET_CRITERIA(1,"get") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Criteria criteria = null;
			if(objects[0] instanceof JSONObject) {
				JSONObject criteriaJson = (JSONObject)objects[0];
				criteria = FieldUtil.getAsBeanFromJson(criteriaJson, Criteria.class);
			}
			else if(objects[0] instanceof Map) {
				Map criteriaMap = (Map)objects[0];
				criteria = FieldUtil.getAsBeanFromMap(criteriaMap, Criteria.class);
			}
			
			return criteria;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	EVALUATE(2,"evaluate") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Criteria criteria = (Criteria)objects[0];
			
			Map<String, Object> data = (Map<String, Object>)objects[1];
			
			Predicate Predicate = criteria.computePredicate(data);
			if(Predicate.evaluate(data)) {
				return true;
			}
			return false;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	AND(3,"and") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Criteria criteria = (Criteria)objects[0];
			
			Criteria criteria1 = (Criteria)objects[1];
			
			criteria.andCriteria(criteria1);
			return criteria;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	OR(4,"or") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Criteria criteria = (Criteria)objects[0];
			
			Criteria criteria1 = (Criteria)objects[1];
			
			criteria.orCriteria(criteria1);
			return criteria;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_CONTAINS_CRITERIA(5,"getContainsCriteria") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			String fieldName = (String)objects[0];
			String value = (String)objects[1];
			
			Condition condition = CriteriaAPI.getCondition(fieldName, value, StringOperators.CONTAINS);
			
			Criteria criteria = new Criteria();
			
			criteria.addAndCondition(condition);
			
			return criteria;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "criteria";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CRITERIA;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioCriteriaFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioCriteriaFunctions> getAllFunctions() {
		return WORKORDER_FUNCTIONS;
	}
	public static FacilioCriteriaFunctions getFacilioCriteriaFunction(String functionName) {
		return WORKORDER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioCriteriaFunctions> WORKORDER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioCriteriaFunctions> initTypeMap() {
		Map<String, FacilioCriteriaFunctions> typeMap = new HashMap<>();
		for(FacilioCriteriaFunctions type : FacilioCriteriaFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
				
}