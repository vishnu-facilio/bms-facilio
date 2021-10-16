package com.facilio.bundle.context;

import java.io.File;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

public class FunctionBundleComponent extends CommonBundleComponent {
	
	public static final String NAME_SPACE = "nameSpace";
	public static final String RETURN_STRING = "return";
	
	public static final String FS_EXTN = "fs";
	
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long functionId = (Long) context.get(BundleConstants.COMPONENT_ID);
		WorkflowUserFunctionContext userFunction = WorkflowV2API.getUserFunction(functionId);
		
		return userFunction.getNameSpaceName()+"_"+userFunction.getName();
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName = BundleComponentsEnum.FUNCTION.getName()+File.separatorChar+getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		bundleBuilder.element(BundleConstants.VALUES).text(fileName);
	}

	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		WorkflowUserFunctionContext function = WorkflowV2API.getUserFunction(componentChange.getComponentId());

		String fileName = getFileName(context);
		
		BundleFolderContext functionNameSpaceFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		BundleFileContext functionXMLFile = new BundleFileContext(fileName, BundleConstants.XML_FILE_EXTN, componentChange.getComponentTypeEnum().getName(), null);
		
		XMLBuilder xmlBuilder = functionXMLFile.getXmlContent();
		
		xmlBuilder.attr(BundleConstants.Components.MODE, componentChange.getModeEnum().getName())
					.attr(NAME_SPACE, function.getNameSpaceName())
				  .element(BundleConstants.Components.NAME)
					.text(function.getName())
					.p()
				  .element(RETURN_STRING)
				    .text(function.getReturnTypeEnum().getStringValue())
				    .p();
		
		functionNameSpaceFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, functionXMLFile);
		
		BundleFileContext functionFile = new BundleFileContext(fileName, FS_EXTN, null, function.getWorkflowV2String());
		
		functionNameSpaceFolder.addFile(fileName+"."+FS_EXTN, functionFile);
		
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		BundleFolderContext parentFolder = (BundleFolderContext) context.get(BundleConstants.BUNDLE_FOLDER);
		
		String scriptContent = parentFolder.getFile(changeSetXMLFile.getName()+"."+FS_EXTN).getFileContent();
		
		BundleModeEnum modeEnum = BundleModeEnum.valueOfName(changeSetXMLFile.getXmlContent().getAttribute(BundleConstants.Components.MODE));
		
		String nameSpaceName = changeSetXMLFile.getXmlContent().getAttribute(NAME_SPACE);
		
		String returnType = changeSetXMLFile.getXmlContent().getElement(RETURN_STRING).getText();
		
		switch(modeEnum) {
		case ADD: 
			
			WorkflowUserFunctionContext userFunction = new WorkflowUserFunctionContext();
			
			userFunction.setWorkflowV2String(scriptContent);
			userFunction.setIsV2Script(Boolean.TRUE);
			userFunction.setReturnType(WorkflowFieldType.getStringvaluemap().get(returnType).getIntValue());
			userFunction.setNameSpaceId(UserFunctionAPI.getNameSpace(nameSpaceName).getId());
			
			FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowUserFunctionChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
			addWorkflowChain.execute();
			
			break;
		}
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
