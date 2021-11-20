package com.facilio.bundle.context;

import java.io.File;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

public class FunctionNameSpaceBundleComponent extends CommonBundleComponent {
	
	
	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long nameSpaceId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		WorkflowNamespaceContext nameSpace = UserFunctionAPI.getNameSpace(nameSpaceId);
		
		return nameSpace.getLinkName();
	}
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long nameSapceId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		WorkflowNamespaceContext nameSpace = UserFunctionAPI.getNameSpace(nameSapceId);

		xmlBuilder.element(BundleConstants.Components.DISPLAY_NAME).text(nameSpace.getName())
				  .p()
				  .element(BundleConstants.Components.NAME).text(nameSpace.getLinkName());
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);
		
		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);
		
		switch(modeEnum) {
		case ADD: {
			WorkflowNamespaceContext workflowNamespaceContext = new WorkflowNamespaceContext();
			
			workflowNamespaceContext.setName(xmlBuilder.getElement(BundleConstants.Components.DISPLAY_NAME).getText());
			workflowNamespaceContext.setLinkName(xmlBuilder.getElement(BundleConstants.Components.NAME).getText());
			workflowNamespaceContext.setSourceBundle(installedBundle.getId());
			
			FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowNameSpaceChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, workflowNamespaceContext);
			addWorkflowChain.execute();
			
			break;
		}
		case UPDATE: {
			WorkflowNamespaceContext workflowNamespaceContext = UserFunctionAPI.getNameSpace(xmlBuilder.getElement(BundleConstants.Components.NAME).getText());
			
			workflowNamespaceContext.setName(xmlBuilder.getElement(BundleConstants.Components.DISPLAY_NAME).getText());
			workflowNamespaceContext.setSourceBundle(installedBundle.getId());
			
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
	public BundleModeEnum getInstallMode(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		String name = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		
		BundleModeEnum installMode = null;
		
		if(UserFunctionAPI.getNameSpace(name) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		return installMode;
		
	}
}
