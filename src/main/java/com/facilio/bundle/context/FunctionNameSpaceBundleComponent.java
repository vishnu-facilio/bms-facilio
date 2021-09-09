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
		
		return nameSpace.getName();
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName = BundleComponentsEnum.FUNCTION_NAME_SPACE.getName()+File.separatorChar+getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		bundleBuilder.element(BundleConstants.VALUES).text(fileName);
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
		
		xmlBuilder.attr(BundleConstants.Components.MODE, componentChange.getModeEnum().getName())
				  .element(BundleConstants.Components.NAME)
					.text(nameSpace.getName())
					.p();
		
		functionNameSpaceFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, nsFile);
		
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		
		BundleModeEnum modeEnum = BundleModeEnum.valueOfName(changeSetXMLFile.getXmlContent().getAttribute(BundleConstants.Components.MODE));
		
		switch(modeEnum) {
		case ADD: 
			
			WorkflowNamespaceContext workflowNamespaceContext = new WorkflowNamespaceContext();
			
			workflowNamespaceContext.setName(changeSetXMLFile.getXmlContent().getElement(BundleConstants.Components.NAME).getText());
			
			FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowNameSpaceChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, workflowNamespaceContext);
			addWorkflowChain.execute();
			
			break;
		}
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
