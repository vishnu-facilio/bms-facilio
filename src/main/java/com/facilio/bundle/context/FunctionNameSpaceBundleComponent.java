package com.facilio.bundle.context;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

public class FunctionNameSpaceBundleComponent extends CommonBundleComponent {

	@Override
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long nameSpaceId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		WorkflowNamespaceContext nameSpace = WorkflowV2API.getNameSpace(nameSpaceId);
		
		return nameSpace.getName();
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
		
		xmlBuilder.element(componentChange.getComponentTypeEnum().getName())
					.attr(BundleConstants.Components.MODE, componentChange.getModeEnum().getName())
				  .element(BundleConstants.Components.NAME)
					.text(nameSpace.getName())
					.p();
		
		functionNameSpaceFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, nsFile);
		
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
