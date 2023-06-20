package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.VariableContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;

import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.services.pdf.*;
import org.json.simple.JSONObject;

import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.CONNECTED_APP_FUNCTION)
public class FacilioConnectedAppFunctions {
	public Object getVariable(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

		if(objects.length < 2) {
			throw new FunctionParamException("Required params is empty. eg: getVariable(connectedAppLinkName, variableName)");
		}
			
			String connectedAppLinkName = objects[0].toString();
			String varName = objects[1].toString();
			
			VariableContext var = ConnectedAppAPI.getVariable(connectedAppLinkName, varName);
			if(var != null) {
				return var.getValue();
			}
			return null;
		}


		public Object setVariable(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			if(objects.length < 3) {
				throw new FunctionParamException("Required params is empty. eg: setVariable(connectedAppLinkName, variableName, value)");
			}

			String connectedAppLinkName = objects[0].toString();
			String varName = objects[1].toString();
			String value = objects[2].toString();

			VariableContext var = ConnectedAppAPI.setVariable(connectedAppLinkName, varName, value);
			if(var != null) {
				return var.getValue();
			}
			return null;
		}



		public Object exportAsPDF(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			if(objects.length < 3) {
				throw new FunctionParamException("Required params is empty. eg: exportAsPDF(fileName, connectedAppLinkName, widgetLinkName, options, context)");
			}

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
		}


		public Object exportAsImage(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

			if (objects.length < 3) {
				throw new FunctionParamException("Required params is empty. eg: exportAsImage(fileName, connectedAppLinkName, widgetLinkName, options, context)");
			}

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
		}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
