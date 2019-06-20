package com.facilio.workflows.functions;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.*;

public enum FacilioModuleFunctions implements FacilioWorkflowFunctionInterface {

	GETMODULE(1,"getModule",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"moduleRefObject") ) {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			String moduleRefObject = objects[0].toString();
			
			if(FacilioUtil.isNumeric(moduleRefObject)) {
				long moduleId = Long.parseLong(objects[0].toString());
				return modBean.getModule(moduleId);
			}
			else {
				return modBean.getModule(moduleRefObject);
			}
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GETFIELD(2,"getField",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"fieldRefObject"),WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"moduleRefObject")) {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			String fieldRefObject = objects[0].toString();
			String moduleRefObject = null;
			
			if(objects.length > 1 && objects[1] != null) {
				moduleRefObject = objects[1].toString();
			}
			
			if(FacilioUtil.isNumeric(fieldRefObject)) {
				long fieldId =(long) Double.parseDouble(fieldRefObject);
				return modBean.getField(fieldId);
			}
			else if(moduleRefObject != null) {
				if(FacilioUtil.isNumeric(moduleRefObject)) {
					Long moduleId = Long.parseLong(moduleRefObject);
					return null;
				}
				else {
					return modBean.getField(fieldRefObject, moduleRefObject);
				}
			}
			

			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GETENUMFIELDVALUE(3,"getEnumFieldValue",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"fieldName"),WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"moduleName"),WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.STRING.getValue(),"enumIntValue")) {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			String fieldName = objects[0].toString();
			String moduleName = objects[1].toString();
			
			FacilioField field = modBean.getField(fieldName, moduleName);
			
			if (field instanceof EnumField) {
				EnumField enumField = (EnumField) field;
				int index = Integer.parseInt(objects[2].toString());
				return enumField.getValue(index);
			}

			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 2) {
				throw new FunctionParamException("Required Object is null -- "+objects);
			}
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "module";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.MODULE;
	
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
	public List<FacilioFunctionsParamType> getParams() {
		return params;
	}
	public void setParams(List<FacilioFunctionsParamType> params) {
		this.params = params;
	}
	public void addParams(FacilioFunctionsParamType param) {
		this.params = (this.params == null) ? new ArrayList<>() :this.params;
		this.params.add(param);
	}
	FacilioModuleFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioModuleFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioModuleFunctions getFacilioModuleFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioModuleFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioModuleFunctions> initTypeMap() {
		Map<String, FacilioModuleFunctions> typeMap = new HashMap<>();
		for(FacilioModuleFunctions type : FacilioModuleFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
