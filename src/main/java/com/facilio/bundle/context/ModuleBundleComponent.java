package com.facilio.bundle.context;

import java.io.File;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.xml.builder.XMLBuilder;

public class ModuleBundleComponent extends CommonBundleComponent {
	
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
		
		xmlBuilder = xmlBuilder.element(BundleConstants.Components.NAME)
									.t(module.getName())
									.p()
							  .element(BundleConstants.Components.DISPLAY_NAME)
							  		.t(module.getDisplayName())
							  		.p();
		
		
		BundleFolderContext moduleFolder = componentFolder.getOrAddFolder(componentChange.getComponentTypeEnum().getName());
		
		moduleFolder.addFile(fileName+"."+BundleConstants.XML_FILE_EXTN, fileContext);
	}
	
	public String getFileName(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		Long moduleId = (Long)context.get(BundleConstants.COMPONENT_ID);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		return modBean.getModule(moduleId).getName();
	}
	
	@Override
	public void fillBundleXML(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName = BundleComponentsEnum.MODULE.getName()+File.separatorChar+getFileName(context)+".xml";
		XMLBuilder bundleBuilder = (XMLBuilder) context.get(BundleConstants.BUNDLE_XML_BUILDER);
		bundleBuilder.element(BundleConstants.VALUES).text(fileName);
	}

//	@Override
//	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
//		// TODO Auto-generated method stub
//		
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		
//		ModuleType[] types = ModuleType.values();
//		
//		JSONArray returnList = new JSONArray();
//		
//		List<FacilioModule> allModules = new ArrayList<FacilioModule>();
//		
//		for(int i=0;i<types.length;i++) {
//			ModuleType moduleType = types[i];
//			
//			if(!getRestricedModuleTypeList().contains(moduleType)) {
//				
//				List<FacilioModule> modules = modBean.getModuleList(moduleType);
//				
//				modules = fetchModuleObject(modules);
//				
//				allModules.addAll(modules);
//			}
//		}
//		
//		List<FacilioModule> modulesByHirarchy = new ArrayList<FacilioModule>();
//		
//		arrangeModuleByHirarchy(allModules,modulesByHirarchy,null);
//		
//		for(FacilioModule module :modulesByHirarchy) {
//			
//			context.put(BundleConstants.COMPONENT_OBJECT, module);
//			context.put(BundleConstants.COMPONENT_ID, module.getModuleId());
//			
//			JSONObject formattedObject = getFormatedObject(context);
//			
//			returnList.add(formattedObject);
//			
//		}
//		
//		return returnList;
//	}
	
	
//	private List<FacilioModule> fetchModuleObject(List<FacilioModule> modules) throws Exception {
//		// TODO Auto-generated method stub
//		
//		List<FacilioModule> modulesObject = new ArrayList<FacilioModule>();
//		
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		
//		for(FacilioModule module : modules) {
//			
//			FacilioModule moduleObj = modBean.getModule(module.getName());
//			modulesObject.add(moduleObj);
//		}
//		return modulesObject;
//	}

//	private void arrangeModuleByHirarchy(List<FacilioModule> modules, List<FacilioModule> modulesByHirarchy,List<Long> parentModuleIds) {
//		// TODO Auto-generated method stub
//		
//		if(modules.isEmpty()) {
//			return;
//		}
//		
//		List<FacilioModule> arrangedModules = new ArrayList<FacilioModule>();
//		for(FacilioModule module : modules) {
//			if(parentModuleIds == null) {
//				if(module.getExtendModule() == null) {
//					arrangedModules.add(module);
//				}
//			}
//			else if (parentModuleIds.contains(module.getExtendModule().getModuleId())) {
//				arrangedModules.add(module);
//			}
//		}
//		
//		modules.removeAll(arrangedModules);
//		
//		modulesByHirarchy.addAll(arrangedModules);
//		
//		if(!modules.isEmpty()) {
//			
//			parentModuleIds = arrangedModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toList());
//			
//			arrangeModuleByHirarchy(modules, modulesByHirarchy, parentModuleIds);
//		}
//	}
////
////	private List<ModuleType> getRestricedModuleTypeList() {
//		
//		List<ModuleType> types = new ArrayList<ModuleType>();
//		
//		types.add(ModuleType.SCHEDULED_FORMULA);
//		types.add(ModuleType.LIVE_FORMULA);
//		types.add(ModuleType.SYSTEM_SCHEDULED_FORMULA);
//		types.add(ModuleType.LOOKUP_REL_MODULE);
//		types.add(ModuleType.ENUM_REL_MODULE);
//		types.add(ModuleType.SLA_TIME);
//		types.add(ModuleType.LARGE_TEXT_DATA_MODULE);
//		
//		return types;
//	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFileContext changeSetXMLFile = (BundleFileContext) context.get(BundleConstants.BUNDLED_XML_COMPONENT_FILE);
		BundleFolderContext parentFolder = (BundleFolderContext) context.get(BundleConstants.BUNDLE_FOLDER);
		
		
//		modBean.addModule(module);
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
