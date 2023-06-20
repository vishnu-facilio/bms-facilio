package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.scriptengine.context.ScriptContext;

@ScriptModule(moduleName = FacilioConstants.ContextNames.BASE_SPACE)
public class FacilioBaseSpaceModuleFunctions extends FacilioModuleFunctionImpl {

	public List<Long> getSubordinates(Map<String,Object> globalParams,List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		List<Long> spaceIds;
		if(!(objects.get(1) instanceof List)) {
			spaceIds = new ArrayList<Long>();
			for(int i=1;i<objects.size();i++) {
				Double id = Double.parseDouble(objects.get(i).toString());
				spaceIds.add(id.longValue());
			}
		}
		else {
			spaceIds = (List<Long>) objects.get(1);
		}
		
		List<BaseSpaceContext> subSpaces = SpaceAPI.getBaseSpaceWithChildren(spaceIds);
		
		if(subSpaces != null && !subSpaces.isEmpty()) {
			List<Long> spaceIds1 = subSpaces.stream()
										.map(BaseSpaceContext::getId)
										.collect(Collectors.toList());
			
			List<ResourceContext> resources = ResourceAPI.getAllResourcesFromSpaces(spaceIds1);
			
			List<Long> resourceIds = new ArrayList<>();
			for(ResourceContext resource :resources) {
				resourceIds.add(resource.getId());
			}
			return resourceIds;							
		}
		return null;
	}
	
}
