package com.facilio.bundle.context;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

public class FunctionNameSpaceBundleComponent extends CommonBundleComponent {
	
	

	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long nameSpaceId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		WorkflowNamespaceContext nameSpace = WorkflowV2API.getNameSpace(nameSpaceId);
		
		return nameSpace.getLinkName();
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		
		String fileName = BundleComponentsEnum.FUNCTION_NAME_SPACE.getName()+File.separatorChar+getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		bundleBuilder.element(BundleConstants.VALUES).attr("version", componentChange.getTempVersion()+"").text(fileName);
	}
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		WorkflowNamespaceContext nameSpace = WorkflowV2API.getNameSpace(componentChange.getComponentId());

		String fileName = getFileName(context);
		
		BundleFolderContext functionNameSpaceFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		BundleFileContext nsFile = new BundleFileContext(fileName, BundleConstants.XML_FILE_EXTN, componentChange.getComponentTypeEnum().getName(), null);
		
		XMLBuilder xmlBuilder = nsFile.getXmlContent();
		
		xmlBuilder.element(BundleConstants.Components.DISPLAY_NAME).text(nameSpace.getName())
				  .p()
				  .element(BundleConstants.Components.NAME).text(nameSpace.getLinkName());
		
		functionNameSpaceFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, nsFile);
		
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		
		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);
		
		switch(modeEnum) {
		case ADD: {
			WorkflowNamespaceContext workflowNamespaceContext = new WorkflowNamespaceContext();
			
			workflowNamespaceContext.setName(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.DISPLAY_NAME).getText());
			workflowNamespaceContext.setLinkName(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.NAME).getText());
			
			FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowNameSpaceChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, workflowNamespaceContext);
			addWorkflowChain.execute();
			
			break;
		}
		case UPDATE: {
			WorkflowNamespaceContext workflowNamespaceContext = UserFunctionAPI.getNameSpace(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.NAME).getText());
			
			workflowNamespaceContext.setName(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.DISPLAY_NAME).getText());
			
			FacilioChain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowNameSpaceChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, workflowNamespaceContext);
			addWorkflowChain.execute();
			
			break;
		}
		}
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		
		XMLBuilder xmlContent = changeSetXMLFile.getXmlContent();
		
		String name = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		
		BundleModeEnum installMode = null;
		
		if(UserFunctionAPI.getNameSpace(name) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		context.put(BundleConstants.INSTALL_MODE, installMode);
		
	}
}
