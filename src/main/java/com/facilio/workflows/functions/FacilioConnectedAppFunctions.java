package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.VariableContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.services.pdf.*;
import org.json.simple.JSONObject;

public enum FacilioConnectedAppFunctions implements FacilioWorkflowFunctionInterface {

	GET_VARIABLE(1,"getVariable") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			String connectedAppLinkName = objects[0].toString();
			String varName = objects[1].toString();
			
			VariableContext var = ConnectedAppAPI.getVariable(connectedAppLinkName, varName);
			if(var != null) {
				return var.getValue();
			}
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 2) {
				throw new FunctionParamException("Required params is empty. eg: getVariable(connectedAppLinkName, variableName)");
			}
		}
	},

	SET_VARIABLE(2,"setVariable") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			String connectedAppLinkName = objects[0].toString();
			String varName = objects[1].toString();
			String value = objects[2].toString();

			VariableContext var = ConnectedAppAPI.setVariable(connectedAppLinkName, varName, value);
			if(var != null) {
				return var.getValue();
			}
			return null;
		};

		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 3) {
				throw new FunctionParamException("Required params is empty. eg: setVariable(connectedAppLinkName, variableName, value)");
			}
		}
	},

	EXPORT_AS_PDF(3, "exportAsPDF") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			String fileName = objects[0].toString();
			String connectedAppLinkName = objects[1].toString();
			String widgetLinkName = objects[2].toString();
			ExportOptions options = null;
			if (objects.length > 3) {
				options = FieldUtil.getAsBeanFromMap((Map<String, Object>) objects[3], PDFOptions.class);
			}
			JSONObject context = new JSONObject();
			if (objects.length > 4) {
				Map<String, Object> contextMap = (Map<String, Object>) objects[4];
				context.putAll(contextMap);
			}

			String linkName = connectedAppLinkName + "." + widgetLinkName;

			return PDFServiceFactory.getPDFService().exportWidget(fileName, linkName, PDFService.ExportType.PDF, options, context);
		};

		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 3) {
				throw new FunctionParamException("Required params is empty. eg: exportAsPDF(fileName, connectedAppLinkName, widgetLinkName, options, context)");
			}
		}
	},

	EXPORT_AS_IMAGE(4, "exportAsImage") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

			checkParam(objects);

			String fileName = objects[0].toString();
			String connectedAppLinkName = objects[1].toString();
			String widgetLinkName = objects[2].toString();
			ExportOptions options = null;
			if (objects.length > 3) {
				options = FieldUtil.getAsBeanFromMap((Map<String, Object>) objects[3], ScreenshotOptions.class);
			}
			JSONObject context = new JSONObject();
			if (objects.length > 4) {
				Map<String, Object> contextMap = (Map<String, Object>) objects[4];
				context.putAll(contextMap);
			}

			String linkName = connectedAppLinkName + "." + widgetLinkName;

			return PDFServiceFactory.getPDFService().exportWidget(fileName, linkName, PDFService.ExportType.SCREENSHOT, options, context);
		};

		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 3) {
				throw new FunctionParamException("Required params is empty. eg: exportAsImage(fileName, connectedAppLinkName, widgetLinkName, options, context)");
			}
		}
	}
	;
	private Integer value;
	private String functionName;
	private String namespace = "connectedApp";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CONNECTED_APP;
	
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
	FacilioConnectedAppFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioConnectedAppFunctions> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioConnectedAppFunctions getFacilioConnectedAppFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioConnectedAppFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioConnectedAppFunctions> initTypeMap() {
		Map<String, FacilioConnectedAppFunctions> typeMap = new HashMap<>();
		for(FacilioConnectedAppFunctions type : FacilioConnectedAppFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
