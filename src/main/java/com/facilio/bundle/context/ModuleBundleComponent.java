package com.facilio.bundle.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;

public class ModuleBundleComponent implements BundleComponentInterface {

	@Override
	public JSONObject getFormatedObject(FacilioContext context) throws Exception {
		
		FacilioModule module = (FacilioModule) context.get(BundleConstants.COMPONENT_OBJECT);
		
		JSONObject returnJson = FieldUtil.getAsJSON(module);
		
		return returnJson;
	}

	@Override
	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ModuleType[] types = ModuleType.values();
		
		JSONArray returnList = new JSONArray();
		
		List<FacilioModule> allModules = new ArrayList<FacilioModule>();
		
		for(int i=0;i<types.length;i++) {
			ModuleType moduleType = types[i];
			
			if(!getRestricedModuleTypeList().contains(moduleType)) {
				
				List<FacilioModule> modules = modBean.getModuleList(moduleType);
				
				modules = fetchModuleObject(modules);
				
				allModules.addAll(modules);
			}
		}
		
		List<FacilioModule> modulesByHirarchy = new ArrayList<FacilioModule>();
		
		arrangeModuleByHirarchy(allModules,modulesByHirarchy,null);
		
		for(FacilioModule module :modulesByHirarchy) {
			
			System.out.println("module Level ::: "+module.getName());
			
//			if(module.getName().equals("agentAlarm") || module.getName().equals("controller")) {
			context.put(BundleConstants.COMPONENT_OBJECT, module);
			context.put(BundleConstants.COMPONENT_ID, module.getModuleId());
			
			JSONObject formattedObject = getFormatedObject(context);
			
			
			returnList.add(formattedObject);
			
//			}
		}
		
		return returnList;
	}
	
	
	private List<FacilioModule> fetchModuleObject(List<FacilioModule> modules) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioModule> modulesObject = new ArrayList<FacilioModule>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(FacilioModule module : modules) {
			
			FacilioModule moduleObj = modBean.getModule(module.getName());
			modulesObject.add(moduleObj);
		}
		return modulesObject;
	}

	private void arrangeModuleByHirarchy(List<FacilioModule> modules, List<FacilioModule> modulesByHirarchy,List<Long> parentModuleIds) {
		// TODO Auto-generated method stub
		
		if(modules.isEmpty()) {
			return;
		}
		
		List<FacilioModule> arrangedModules = new ArrayList<FacilioModule>();
		for(FacilioModule module : modules) {
			if(parentModuleIds == null) {
				if(module.getExtendModule() == null) {
					arrangedModules.add(module);
				}
			}
			else if (parentModuleIds.contains(module.getExtendModule().getModuleId())) {
				arrangedModules.add(module);
			}
		}
		
		modules.removeAll(arrangedModules);
		
		modulesByHirarchy.addAll(arrangedModules);
		
		if(!modules.isEmpty()) {
			
			parentModuleIds = arrangedModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toList());
			
			arrangeModuleByHirarchy(modules, modulesByHirarchy, parentModuleIds);
		}
	}

	private List<ModuleType> getRestricedModuleTypeList() {
		
		List<ModuleType> types = new ArrayList<ModuleType>();
		
		types.add(ModuleType.SCHEDULED_FORMULA);
		types.add(ModuleType.LIVE_FORMULA);
		types.add(ModuleType.SYSTEM_SCHEDULED_FORMULA);
		types.add(ModuleType.LOOKUP_REL_MODULE);
		types.add(ModuleType.ENUM_REL_MODULE);
		types.add(ModuleType.SLA_TIME);
		types.add(ModuleType.LARGE_TEXT_DATA_MODULE);
		
		return types;
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject moduleJSON = (JSONObject) context.get(BundleConstants.COMPONENT_OBJECT);
		
		FacilioModule module = FieldUtil.getAsBeanFromJson(moduleJSON, FacilioModule.class);
		
		module.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(module.getExtendModule() != null) {
			
			FacilioModule extendedModule = modBean.getModule(module.getExtendModule().getName());
			if(extendedModule == null) {
				throw new Exception(module.getName()+" extendedModule is null :: "+module.getExtendModule().getName());
			}
			module.setExtendModule(extendedModule);
		}
		modBean.addModule(module);
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
