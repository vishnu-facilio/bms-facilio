package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

import io.jsonwebtoken.lang.Collections;

public class ConstuctDataForBulkCreateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<ModuleBaseWithCustomFields> resources= (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);
		
		JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);
		
		String dataModuleName = (String) context.get(FacilioConstants.ContextNames.DATA_MODULE_NAME);
		
		String resourceFieldName = getResourceFieldNameForModule(dataModuleName);
		
		if(!Collections.isEmpty(resources)) {
			
			List<Map<String,Object>> propsList = new ArrayList<>();
			
			for(ModuleBaseWithCustomFields resource : resources) {
				
				Map<String,Object> prop = new HashMap<>();
				prop.put(resourceFieldName, FieldUtil.getAsProperties(resource));
				
				if(data != null) {
					Iterator<String> itrr = data.keySet().iterator();
					while(itrr.hasNext()) {
						String key = itrr.next();
						prop.put(key, data.get(key));
					}
				}
				
				propsList.add(prop);
			}
			
			context.put(FacilioConstants.ContextNames.MODULE_DATA_LIST,propsList);
			
		}
		
		return false;
	}

	private String getResourceFieldNameForModule(String moduleName) throws Exception {
		
		switch(moduleName) {
		case FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER:
			return "resource";
		default:
			throw new Exception("Unsupported module for bulk create - "+moduleName);
		}
		
	}

}
