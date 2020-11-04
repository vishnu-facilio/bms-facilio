package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ResourceAPI {

	private static final Logger LOGGER = LogManager.getLogger(ReadingsAPI.class.getName());

	public static ResourceContext getResource(long id) throws Exception {
		return getResource(id, false);
	}

	public static ResourceContext getResource(long id, boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
				.select(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE)).module(module)
				.beanClass(ResourceContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
		if (fetchDeleted) {
			resourceBuilder.fetchDeleted();
		}

		List<ResourceContext> resources = resourceBuilder.get();
		if (resources != null && !resources.isEmpty()) {
			return resources.get(0);
		}
		return null;
	}

	public static List<ResourceContext> getResources(List<Long> ids, boolean fetchDeleted) throws Exception {

		if (ids == null || ids.isEmpty()) {
			return null;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>().select(fields)
				.module(module).beanClass(ResourceContext.class).andCondition(CriteriaAPI.getIdCondition(ids, module));

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
			case CONTROLLER:
				return AssetsAPI.getAssetInfo(id);
			case SPACE:
				return SpaceAPI.getBaseSpace(id);
			default:
				return resource;
			}
		}
		return null;
	}

	public static List<ResourceContext> getAllResourcesFromSpaces(List<Long> spaceIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		FacilioField spaceField = FieldFactory.getAsMap(fields).get("space");
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
				.select(fields).module(module).beanClass(ResourceContext.class)
				.andCondition(CriteriaAPI.getCondition(spaceField, spaceIds, NumberOperators.EQUALS));

		List<ResourceContext> resources = resourceBuilder.get();
		return resources;
	}

	public static List<ResourceContext> getAllResources() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
				.select(fields).module(module).beanClass(ResourceContext.class);

		List<ResourceContext> resources = resourceBuilder.get();
		// LOGGER.error("builder -- "+resourceBuilder);
		return resources;
	}
	
	// Use this when only name for resource is needed
	public static Map<Long, Map<String, Object>> getResourceMapFromIds(Collection<Long> resourceIds, boolean fetchDeleted) throws Exception {
		if (resourceIds == null || resourceIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE));
		List<FacilioField> fields = new ArrayList<>();
		fields.add(fieldMap.get("name"));
		fields.add(fieldMap.get("resourceType"));

		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>().select(fields)
				.module(module).beanClass(ResourceContext.class)
				.andCondition(CriteriaAPI.getIdCondition(resourceIds, module));
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		return selectBuilder.getAsMapProps();
	}

	public static Map<Long, ResourceContext> getResourceAsMapFromIds(Collection<Long> resourceIds) throws Exception {

		if (resourceIds == null || resourceIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);

		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>().select(fields)
				.module(module).beanClass(ResourceContext.class)
				.andCondition(CriteriaAPI.getIdCondition(resourceIds, module));
		return selectBuilder.getAsMap();
	}

	public static List<ResourceContext> getExtendedResources(List<Long> resourceIds, boolean fetchDeleted)
			throws Exception {
		List<ResourceContext> resources = getResources(resourceIds, fetchDeleted);
		if (resources != null && !resources.isEmpty()) {
			Set<Long> spaceIds = new HashSet<Long>();
			Set<Long> assetIds = new HashSet<Long>();

			for (ResourceContext resource : resources) {
				switch (resource.getResourceTypeEnum()) {
				case SPACE:
					spaceIds.add(resource.getId());
					break;
				case CONTROLLER:
				case ASSET:
					assetIds.add(resource.getId());
					if (resource.getSpaceId() != -1) {
						spaceIds.add(resource.getSpaceId());
					}
					break;
				default:
					break;
				}
			}
			Map<Long, BaseSpaceContext> spaceMap = spaceIds.isEmpty() ? null : getSpaces(spaceIds, fetchDeleted);
			Map<Long, AssetContext> assetMap = assetIds.isEmpty() ? null : getAssets(assetIds, fetchDeleted);

			for (int i = 0; i < resources.size(); i++) {
				ResourceContext resource = resources.get(i);
				switch (resource.getResourceTypeEnum()) {
				case SPACE:
					BaseSpaceContext space = spaceMap.get(resource.getId());
					resources.set(i, space);
					break;
				case ASSET:
					AssetContext asset = assetMap.get(resource.getId());
					if (asset.getSpaceId() != -1) {
						asset.setSpace(spaceMap.get(asset.getSpaceId()));
					}
					resources.set(i, asset);
					break;
				default:
					break;
				}
			}
			return resources;
		}
		return null;
	}

	public static Map<Long, ResourceContext> getExtendedResourcesAsMapFromIds(List<Long> resourceIds,
			boolean fetchDeleted) throws Exception {
		List<ResourceContext> resources = getExtendedResources(resourceIds, fetchDeleted);
		Map<Long, ResourceContext> resourceMap = new HashMap<>();
		if (resources != null && !resources.isEmpty()) {
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
				.select(fields).module(module).beanClass(BaseSpaceContext.class)
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

		SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>().select(fields)
				.module(module).beanClass(AssetContext.class)
				.andCondition(CriteriaAPI.getIdCondition(assetIds, module));
		if (fetchDeleted) {
			selectBuilder.fetchDeleted();
		}
		return selectBuilder.getAsMap();
	}

	public static List<FacilioModule> getResourceSpecificReadings(long parentId) throws Exception {
		List<FacilioField> fields = FieldFactory.getResourceReadingsFields();
		FacilioField resourceField = FieldFactory.getAsMap(fields).get("resourceId");
		FacilioModule module = ModuleFactory.getResourceReadingsModule();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(resourceField, String.valueOf(parentId), PickListOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			List<FacilioModule> readings = new ArrayList<>();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (Map<String, Object> prop : props) {
				readings.add(modBean.getModule((long) prop.get("readingId")));
			}
			return readings;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public static void loadModuleResources(Collection<? extends ModuleBaseWithCustomFields> records,
			List<FacilioField> fields) throws Exception {
		if (records != null && !records.isEmpty()) {
			List<FacilioField> resourceFields = fields.stream().filter(field -> {
				if (field.getDataTypeEnum() == FieldType.LOOKUP && ((LookupField) field).getLookupModule() != null
						&& ((LookupField) field).getLookupModule().getExtendModule() != null) {
					FacilioModule extendedModule = ((LookupField) field).getLookupModule().getExtendModule();
					return extendedModule.getName() != null
							&& (extendedModule.getName().equals(FacilioConstants.ContextNames.RESOURCE)
									|| extendedModule.getName().equals(FacilioConstants.ContextNames.BASE_SPACE));
				}
				return false;
			}).collect(Collectors.toList());
			if (!resourceFields.isEmpty()) {
				List<Long> resourceIds = new ArrayList<>();
				for (ModuleBaseWithCustomFields record : records) {
					if (record.getData() != null) {
						for (FacilioField resourceField : resourceFields) {
							if (record.getData().containsKey(resourceField.getName())) {
								ModuleBaseWithCustomFields resource = (ModuleBaseWithCustomFields) record.getData()
										.get(resourceField.getName());
								if ((resource.getId() != -1)) {
									resourceIds.add(resource.getId());
								}
							}
						}
					}
				}
				if (resourceIds.isEmpty()) {
					return;
				}

				Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
				if (resources != null && !resources.isEmpty()) {
					for (ModuleBaseWithCustomFields record : records) {
						if (record.getData() != null) {
							for (FacilioField resourceField : resourceFields) {
								if (record.getData().containsKey(resourceField.getName())) {
									ModuleBaseWithCustomFields resource = (ModuleBaseWithCustomFields) record.getData()
											.get(resourceField.getName());
									if (resource != null && (resource.getId() != -1)) {
										long resourceId = ((ModuleBaseWithCustomFields) record.getData()
												.get(resourceField.getName())).getId();
										record.getData().put(resourceField.getName(), resources.get(resourceId));
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static long getSiteIDForSpaceOrAsset(long resourceId) throws Exception {
		ResourceContext resource = ResourceAPI.getResource(resourceId);
		long siteId = -1;
		if (resource.getResourceTypeEnum() == ResourceType.SPACE) {
			BaseSpaceContext space = SpaceAPI.getBaseSpace(resourceId);
			siteId = space.getSiteId();
		} else {
			AssetContext asset = AssetsAPI.getAssetInfo(resourceId);
			if (asset.getSpaceId() != -1) {
				BaseSpaceContext space = SpaceAPI.getBaseSpace(asset.getSpaceId());
				siteId = space.getSiteId();
			}
		}
		return siteId;
	}
}
