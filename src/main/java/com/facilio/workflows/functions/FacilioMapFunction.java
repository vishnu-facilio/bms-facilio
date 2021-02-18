package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.util.FacilioUtil;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioMapFunction implements FacilioWorkflowFunctionInterface {

	
	CREATE(1,"create") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> map = new HashMap<>();
			return map;
		};
		
		public void checkParam(Object... objects) throws Exception {

		}
	},
	PUT(2,"put") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> map = (Map<String, Object>) objects[0];
			Object key = objects[1];
			Object value = objects[2];
			
			if(key != null) {
				map.put(key.toString(), value);
			}
			
			return map;
		};
		
		public void checkParam(Object... objects) throws Exception {

			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	PUT_ALL(3,"putAll") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> map = (Map<String, Object>) objects[0];
			Map<String,Object> map1 = (Map<String, Object>) objects[1];
			
			map.putAll(map1);
			
			return map;
		};
		
		public void checkParam(Object... objects) throws Exception {

			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	REMOVE(4,"remove") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> map = (Map<String, Object>) objects[0];
			Object key = objects[1];
			if(key != null) {
				map.remove(key.toString());
			}
			
			return map;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	REMOVE_ALL(5,"removeAll") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			Map<String,Object> map = (Map<String, Object>) objects[0];
			List<Object> keys = (List<Object>) objects[1];
			
			for(Object key :keys) {
				if(key != null) {
					map.remove(key.toString());
				}
			}
			return map;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	CLEAR(6,"clear") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Map<String,Object> map = (Map<String, Object>) objects[0];
			map.clear();
			return map;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET(7,"get") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			Map<String,Object> map = (Map<String, Object>) objects[0];
			Object key = objects[1];
			if(key != null) {
				return map.get(key.toString());
			}
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length <= 1) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SIZE_OF(8,"size") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			Map<String,Object> map = (Map<String, Object>) objects[0];
			return map.size();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length < 1) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	PARSE(9, "parse") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects[0] instanceof Map) {
				org.json.JSONObject json = new org.json.JSONObject((Map)objects[0]);
				return FacilioUtil.parseJson(json.toString());
			}
			String string = (String) objects[0];
			
			return FacilioUtil.parseJson(string);
			
		}
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length < 1) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	TO_STRING(10, "toString") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects[0] instanceof Map) {
				org.json.JSONObject json = new org.json.JSONObject((Map)objects[0]);
				return json.toString();
			}
			String string = (String) objects[0];
			
			return FacilioUtil.parseJson(string).toJSONString();
			
		}
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length < 1) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	KEYS(11, "keys") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			Map<String,Object> map = (Map<String, Object>) objects[0];
			return new ArrayList<String>(map.keySet());
			
		}
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length < 1) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
		
	;
	private Integer value;
	private String functionName;
	private String namespace = "map";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.MAP;
	
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
	FacilioMapFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioMapFunction> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioMapFunction getFacilioMapFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	private static final Map<String, FacilioMapFunction> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	private static Map<String, FacilioMapFunction> initTypeMap() {
		Map<String, FacilioMapFunction> typeMap = new HashMap<>();
		for(FacilioMapFunction type : FacilioMapFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
