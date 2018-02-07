package com.facilio.bmsconsole.util;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ResourceAPI {
	public static ResourceContext getResource(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																		.module(module)
																		.beanClass(ResourceContext.class)
																		.andCondition(CriteriaAPI.getIdCondition(id, module))
																		;
		
		List<ResourceContext> resources = resourceBuilder.get();
		if(resources != null && !resources.isEmpty()) {
			return resources.get(0);
		}
		return null;									
	}
	
	public static List<ResourceContext> getAllResourcesFromSpaces(List<Long> spaceIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		FacilioField spaceField = FieldFactory.getAsMap(fields).get("spaceId");
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(fields)
																		.module(module)
																		.beanClass(ResourceContext.class)
																		.andCondition(CriteriaAPI.getCondition(spaceField, spaceIds, NumberOperators.EQUALS))
																		;
		
		List<ResourceContext> resources = resourceBuilder.get();
		return resources;									
	}
}
