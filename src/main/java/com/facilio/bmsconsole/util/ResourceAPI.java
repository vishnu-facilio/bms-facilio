package com.facilio.bmsconsole.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
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
	
	public static List<ResourceContext> getResources (List<Long> ids, boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		
		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(fields)
																		.module(module)
																		.beanClass(ResourceContext.class)
																		.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		return selectBuilder.get();
	}
	
	public static ResourceContext getExtendedResource(long id) throws Exception {
		ResourceContext resource = getResource(id);
		if (resource != null) {
			switch (resource.getResourceTypeEnum()) {
				case ASSET:
					return AssetsAPI.getAssetInfo(id);
				case SPACE:
					return SpaceAPI.getBaseSpace(id);
			}
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
	
	public static List<ResourceContext> getAllResources() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(fields)
																		.module(module)
																		.beanClass(ResourceContext.class);
		
		List<ResourceContext> resources = resourceBuilder.get();
		return resources;									
	}
	
	public static Map<Long, ResourceContext> getResourceAsMapFromIds (Collection<Long> resourceIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		
		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>()
																		.select(fields)
																		.module(module)
																		.beanClass(ResourceContext.class)
																		.andCondition(CriteriaAPI.getIdCondition(resourceIds, module));
		return selectBuilder.getAsMap();
	}
	
	public static List<ResourceContext> getExtendedResources (List<Long> resourceIds, boolean fetchDeleted) throws Exception {
		List<ResourceContext> resources = getResources(resourceIds, fetchDeleted);
		if(resources != null && !resources.isEmpty()) {
			Set<Long> spaceIds = new HashSet<Long>();
			Set<Long> assetIds = new HashSet<Long>();
			
			for(ResourceContext resource : resources) {
				switch (resource.getResourceTypeEnum()) {
					case SPACE:
						spaceIds.add(resource.getId());
						break;
					case ASSET:
						assetIds.add(resource.getId());
						if(resource.getSpaceId() != -1) {
							spaceIds.add(resource.getSpaceId());
						}
						break;
				}
			}
			Map<Long, BaseSpaceContext> spaceMap = getSpaces(spaceIds, fetchDeleted);
			Map<Long, AssetContext> assetMap = getAssets(assetIds, fetchDeleted);
			
			for(int i = 0; i < resources.size(); i++) {
				ResourceContext resource = resources.get(i);
				switch (resource.getResourceTypeEnum()) {
					case SPACE:
						BaseSpaceContext space = spaceMap.get(resource.getId());
						resources.set(i, space);
						break;
					case ASSET:
						AssetContext asset = assetMap.get(resource.getId());
						if(asset.getSpaceId() != -1) {
							asset.setSpace(spaceMap.get(asset.getSpaceId()));
						}
						resources.set(i, asset);
						break;
				}
			}
			return resources;
		}
		return null;
	}
	
	public static Map<Long, ResourceContext> getExtendedResourcesAsMapFromIds (List<Long> resourceIds, boolean fetchDeleted) throws Exception {
		List<ResourceContext> resources = getExtendedResources(resourceIds, fetchDeleted);
		Map<Long, ResourceContext> resourceMap = new HashMap<>();
		if(resources != null && !resources.isEmpty()) {
			for (ResourceContext resource : resources) {
				resourceMap.put(resource.getId(), resource);
			}
		}
		return resourceMap;
	}
	
	private static Map<Long, BaseSpaceContext> getSpaces(Set<Long> spaceIds, boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
																	.select(fields)
																	.module(module)
																	.beanClass(BaseSpaceContext.class)
																	.andCondition(CriteriaAPI.getIdCondition(spaceIds, module));
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		return selectBuilder.getAsMap();
	}
	
	private static Map<Long, AssetContext> getAssets(Set<Long> assetIds, boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		
		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
																	.select(fields)
																	.module(module)
																	.beanClass(AssetContext.class)
																	.andCondition(CriteriaAPI.getIdCondition(assetIds, module));
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		return selectBuilder.getAsMap();
	}
}
