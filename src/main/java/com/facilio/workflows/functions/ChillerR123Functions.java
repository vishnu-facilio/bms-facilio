package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.facilio.workflow.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

public enum ChillerR123Functions implements FacilioWorkflowFunctionInterface {

	GET_TEMP_FROM_PRESSURE(1,"getTempratureFromPresure") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			double presure = Double.parseDouble(objects[0].toString());
			
			Map<Double, Double> chillerMap = WorkflowUtil.getChillerTempVsPressureMap();
			
			if(chillerMap.get(presure) != null ) {
				return chillerMap.get(presure);
			}
			else {
				TreeSet<Double> keySet = new TreeSet<Double>();
				
				keySet.addAll(chillerMap.keySet());

				Double lesserValue = null,greaterValue = null;
				Double lesserValueDelta = null,greaterValueDelta = null;
				
				Object[] keysetArray = keySet.toArray();
				
				for(int i=0;i<keysetArray.length;i++) {
					
					double key = (double) keysetArray[i];
					if(presure < key) {
						if( i== 0) {
							return chillerMap.get(key);
						}
						greaterValue = key;
						lesserValue = (double) keysetArray[i-1];
						break;
					}
				}
				lesserValueDelta = presure - lesserValue;
				greaterValueDelta = greaterValue - presure;
				
				if(lesserValueDelta > greaterValueDelta) {
					return chillerMap.get(greaterValue);
				}
				else {
					return chillerMap.get(lesserValue);
				}
			}
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "chiller.r123";
	private String params;
	
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
	ChillerR123Functions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static ChillerR123Functions getChillerR123Function(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, ChillerR123Functions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, ChillerR123Functions> initTypeMap() {
		Map<String, ChillerR123Functions> typeMap = new HashMap<>();
		for(ChillerR123Functions type : ChillerR123Functions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}