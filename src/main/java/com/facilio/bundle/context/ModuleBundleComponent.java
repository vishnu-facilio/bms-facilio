package com.facilio.bundle.context;

import java.io.File;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

public class ModuleBundleComponent extends CommonBundleComponent {
	
	public static final String DESCRIPTION = "description";
	public static final String STATEFLOW_ENABLED = "stateFlowEnabled";
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		BundleFolderContext componentFolder = (BundleFolderContext) context.get(BundleConstants.COMPONENTS_FOLDER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(componentChange.getComponentId());
		
		String fileName = getFileName(context);
		
		BundleFileContext fileContext = new BundleFileContext(fileName, BundleConstants.XML_FILE_EXTN, componentChange.getComponentTypeEnum().getName(), null);
		
		XMLBuilder xmlBuilder = fileContext.getXmlContent();
		
		xmlBuilder.attr(BundleConstants.Components.MODE, componentChange.getModeEnum().getName());
		
		if(componentChange.getModeEnum() != BundleModeEnum.DELETE) {
			xmlBuilder = xmlBuilder.element(BundleConstants.Components.NAME).t(module.getName()).p()
								   .element(BundleConstants.Components.DISPLAY_NAME).t(module.getDisplayName()).p()
								   .element(DESCRIPTION).t(module.getDescription()).p()
								   .element(STATEFLOW_ENABLED).t(module.isStateFlowEnabled().toString()).p()
								   ;
		}
		
		BundleFolderContext moduleFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		moduleFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, fileContext);
	}
	
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleChangeSetContext componentChange = (BundleChangeSetContext) context.get(BundleConstants.BUNDLE_CHANGE);
		
		return componentChange.getComponentName();
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName = BundleComponentsEnum.MODULE.getName()+File.separatorChar+getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		bundleBuilder.element(BundleConstants.VALUES).text(fileName);
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		
		XMLBuilder xmlContent = changeSetXMLFile.getXmlContent();
		BundleModeEnum modeEnum = BundleModeEnum.valueOfName(xmlContent.getAttribute(BundleConstants.Components.MODE));
		
		String name = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		String displayName = xmlContent.getElement(BundleConstants.Components.DISPLAY_NAME).getText();
		String description = xmlContent.getElement(DESCRIPTION).getText();
		
		Boolean stateFlowEnabled = Boolean.valueOf(xmlContent.getElement(STATEFLOW_ENABLED).getText());
		
		switch(modeEnum) {
		case ADD: 
			
			FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();
			FacilioContext newContext = addModulesChain.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_TYPE, 0);
			newContext.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
			newContext.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
			newContext.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);

			addModulesChain.execute();
			
			break;
		}
		
//		modBean.addModule(module);
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
