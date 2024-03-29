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
		
		IGNORE_MODULE_TYPES.add(ModuleType.PHOTOS.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.ATTACHMENTS.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.NOTES.getValue());
		
		IGNORE_MODULE_TYPES.add(ModuleType.SCHEDULED_FORMULA.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.LIVE_FORMULA.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.SYSTEM_SCHEDULED_FORMULA.getValue());
		
		IGNORE_MODULE_TYPES.add(ModuleType.ACTIVITY.getValue());
		
		IGNORE_MODULE_TYPES.add(ModuleType.ENUM_REL_MODULE.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.LOOKUP_REL_MODULE.getValue());
		IGNORE_MODULE_TYPES.add(ModuleType.LARGE_TEXT_DATA_MODULE.getValue());
	}
	
	@Override
	public Condition getFetchChangeSetCondition(FacilioContext context) throws Exception {
		
		Condition changeSetFetchCondition = CriteriaAPI.getCondition("MODULE_TYPE", "type", StringUtils.join(IGNORE_MODULE_TYPES, ","), NumberOperators.NOT_EQUALS);
		
		return changeSetFetchCondition;
	}
	
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception {
		
		Long moduleId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleId);
		
		xmlBuilder = xmlBuilder.element(BundleConstants.Components.NAME).t(module.getName()).p()
							   .element(BundleConstants.Components.DISPLAY_NAME).t(module.getDisplayName()).p()
							   .element(DESCRIPTION).t(module.getDescription()).p()
							   .element(STATEFLOW_ENABLED).t(module.isStateFlowEnabled().toString()).p()
							   ;
	}
	
	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		long moduleID = (long) context.get(BundleConstants.COMPONENT_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleID);
		
		return module.getName();
	}
	
	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);
		
		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);
		
		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);
		
		String name = xmlContent.getElement(BundleConstants.Components.NAME).getText();
		String displayName = xmlContent.getElement(BundleConstants.Components.DISPLAY_NAME).getText();
		String description = xmlContent.getElement(DESCRIPTION).getText();
		
		Boolean stateFlowEnabled = Boolean.valueOf(xmlContent.getElement(STATEFLOW_ENABLED).getText());
		
		switch(modeEnum) {
		case ADD: {
			FacilioChain addModulesChain = TransactionChainFactory.getAddModuleChain();
			FacilioContext newContext = addModulesChain.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_TYPE, 0);
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME,name);
			newContext.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
			newContext.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
			newContext.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);

			addModulesChain.execute();
			
			// to update installed bundle id
			FacilioChain updateChain = TransactionChainFactory.getUpdateModuleChain();
			newContext = updateChain.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, name);
			newContext.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
			newContext.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
			newContext.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);
			
			newContext.put(BundleConstants.INSTALLED_BUNDLE, installedBundle);
			
			updateChain.execute();
			
			break;
		}
			
		case UPDATE:  {
			
			FacilioChain chain = TransactionChainFactory.getUpdateModuleChain();
			FacilioContext newContext = chain.getContext();
			newContext.put(FacilioConstants.ContextNames.MODULE_NAME, name);
			newContext.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, displayName);
			newContext.put(FacilioConstants.ContextNames.MODULE_DESCRIPTION, description);
			newContext.put(FacilioConstants.ContextNames.STATE_FLOW_ENABLED, stateFlowEnabled);
			newContext.put(BundleConstants.INSTALLED_BUNDLE, installedBundle);

			chain.execute();

			break;
		}
		}
		
//		modBean.addModule(module);
		
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
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		BundleModeEnum installMode = null;
		
		if(modBean.getModule(name) != null) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}
		
		return installMode;
		
	}

}
