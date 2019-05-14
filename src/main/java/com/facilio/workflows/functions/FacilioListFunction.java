package com.facilio.workflows.functions;

import com.facilio.util.FacilioUtil;
import com.facilio.workflows.exceptions.FunctionParamException;

import java.util.*;

public enum FacilioListFunction implements FacilioWorkflowFunctionInterface {

	CREATE_LIST(1,"create") {
		@Override
		public Object execute(Object... objects) throws Exception {
			checkParam();
			
			List<Object> list = new ArrayList<>();
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD(2,"add") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			List<Object> list = (List<Object>) objects[0];
			Object element = objects[1];
			
			list.add(element);
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	REMOVE(3,"remove") {
		@Override
		public Object execute(Object... objects) throws Exception {
			 
			checkParam(objects);
			
			List<Object> list = (List<Object>) objects[0];
			int pos = (int) objects[1];
			
			list.remove(pos);
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	REMOVE_ELEMENT(4,"removeElement") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			List<Object> list = (List<Object>) objects[0];
			Object element = objects[1];
			
			list.remove(element);
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	CLEAR(5,"clear") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			List<Object> list = (List<Object>) objects[0];
			Object element = objects[1];
			
			list.remove(element);
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SORT(6,"sort") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			List<Object> list = (List<Object>) objects[0];
			if(list.get(0) != null) {
				if(list.get(0) instanceof String) {
					List<String> list1 = new ArrayList<>();
					for(Object key :list) {
						list1.add(key.toString());
					}
					
					Collections.sort(list1);
					return list1;
				}
				else if (FacilioUtil.isNumeric(list.get(0).toString())) {
					List<Double> list1 = new ArrayList<>();
					for(Object key :list) {
						list1.add(Double.parseDouble(key.toString()));
					}
					Collections.sort(list1);
					return list1;
				}
			}
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	ADD_ALL(7,"addAll") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			List<Object> list = (List<Object>) objects[0];
			List<Object> list1 = (List<Object>) objects[1];
			
			list.addAll(list1);
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	REMOVE_ALL(8,"removeAll") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			List<Object> list = (List<Object>) objects[0];
			List<Object> list1 = (List<Object>) objects[1];
			
			list.removeAll(list1);
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SIZE_OF(9,"size") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			List<Object> list = (List<Object>) objects[0];
			
			return list.size();
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
	private String namespace = "list";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.LIST;
	
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
	FacilioListFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioListFunction> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	
	public static FacilioListFunction getFacilioListFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioListFunction> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioListFunction> initTypeMap() {
		Map<String, FacilioListFunction> typeMap = new HashMap<>();
		for(FacilioListFunction type : FacilioListFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
