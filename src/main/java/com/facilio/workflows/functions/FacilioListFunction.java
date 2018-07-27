package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.workflow.exceptions.FunctionParamException;

public enum FacilioListFunction implements FacilioWorkflowFunctionInterface {

	CREATE_LIST(1,"create") {
		@Override
		public Object execute(Object... objects) throws Exception {
			checkParam();
			
			List<Object> list = new ArrayList<>();
			return list;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	ADD(2,"add") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam();
			
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
			 
			checkParam();
			
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
			
			checkParam();
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
			
			checkParam();
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
			
			checkParam();
			List<Object> list = (List<Object>) objects[0];
			List<String> list1 = new ArrayList<>();
			for(Object key :list) {
				list1.add(key.toString());
			}
			
			Collections.sort(list1);
			return list1;
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
			
			checkParam();
			
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
			
			checkParam();
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
	;
	private Integer value;
	private String functionName;
	private String namespace = "list";
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
	FacilioListFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
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
