package com.facilio.bundle.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.xml.builder.XMLBuilder;

public class ModuleBundleComponent extends CommonBundleComponent {
	
	public static final String DESCRIPTION = "description";
	public static final String STATEFLOW_ENABLED = "stateFlowEnabled";
	
	public static final List<Integer> IGNORE_MODULE_TYPES = new ArrayList<Integer>();
	
	static {
		
		IGNORE_MODULE_TYPES.add(ModuleType.ATTACHMENTS.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.NOTES.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.ENUM_REL_MODULE.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.LARGE_TEXT_DATA_MODULE.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.LOOKUP_REL_MODULE.getValue());
	}
	
	@Override
	public Condition getFetchChangeSetCondition(FacilioContext context) throws Exception {
		
		Condition changeSetFetchCondition = CriteriaAPI.getCondition("MODULE_TYPE", "type", StringUtils.join(IGNORE_MODULE_TYPES, ","), NumberOperators.NOT_EQUALS);
		
		return changeSetFetchCondition;
	}
	
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
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(componentChange.getComponentId());
		
		return module.getName();
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
