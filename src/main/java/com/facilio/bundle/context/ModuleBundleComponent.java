package com.facilio.bundle.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
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
		
		for(int i=0;i<types.length;i++) {
			ModuleType moduleType = types[i];
			
			if(!getRestricedModuleTypeList().contains(moduleType)) {
				
				List<FacilioModule> modules = modBean.getModuleList(moduleType);
				
				for(FacilioModule module :modules) {
					
					context.put(BundleConstants.COMPONENT_OBJECT, module);
					context.put(BundleConstants.COMPONENT_ID, module.getModuleId());
					
					JSONObject formattedObject = getFormatedObject(context);
					
					returnList.add(formattedObject);
				}
				
			}
		}
		
		return returnList;
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
	public void install(FacilioContext context) {
		// TODO Auto-generated method stub
		
	}

}
