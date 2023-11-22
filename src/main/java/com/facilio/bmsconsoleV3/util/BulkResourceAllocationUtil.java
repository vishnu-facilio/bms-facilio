package com.facilio.bmsconsoleV3.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext.JPScopeAssignmentType;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.util.MeterUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

@Log4j
public class BulkResourceAllocationUtil {
	
	public static enum BulkAllocationTypes implements FacilioIntEnum {
        RESOURCE {
			@Override
			String getName(ModuleBaseWithCustomFields moduleBase) {
				// TODO Auto-generated method stub
				return ((ResourceContext) moduleBase).getName();
			}
		},
        METER {
			@Override
			String getName(ModuleBaseWithCustomFields moduleBase) {
				// TODO Auto-generated method stub
				return ((V3MeterContext) moduleBase).getName();
			}
		};

        public static BulkAllocationTypes valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
        
        abstract String getName(ModuleBaseWithCustomFields moduleBase);
    }

	
	public static List<Long> getMultipleResourceIdToBeAddedFromPM(PMAssignmentType pmAssignmentType, List<Long> resourceIds,Long spaceCategoryID,Long assetCategoryID,Long currentAssetId, List<PMIncludeExcludeResourceContext> includeExcludeRess, boolean isMultiSite) throws Exception {
		
		List<ResourceContext> resources = getMultipleResourceToBeAddedFromPM(pmAssignmentType, resourceIds, spaceCategoryID, assetCategoryID, currentAssetId, includeExcludeRess, isMultiSite);
		
		if(!CollectionUtils.isEmpty(resources)) {
			return resources.stream().map((res) -> res.getId()).collect(Collectors.toList());
		}
		return null;
	}
	
	public static List<ResourceContext> getMultipleResourceToBeAddedFromPM(PMAssignmentType pmAssignmentType, List<Long> resourceIds,Long spaceCategoryID,Long assetCategoryID,Long currentAssetId, List<PMIncludeExcludeResourceContext> includeExcludeRess, boolean isMultiSite) throws Exception {
		
		Map<BulkAllocationTypes,List<ModuleBaseWithCustomFields>> returnObj = getMultipleObjFromBulkConfig(pmAssignmentType, resourceIds, spaceCategoryID, assetCategoryID, currentAssetId, includeExcludeRess, isMultiSite, null,null);
		
		if(returnObj != null && returnObj.get(BulkAllocationTypes.METER) != null && !returnObj.get(BulkAllocationTypes.METER).isEmpty()) {
			throw new RuntimeException("Meter Type not supported here");
		}
		
		if(returnObj != null && returnObj.get(BulkAllocationTypes.RESOURCE) != null) {
			
			List<ModuleBaseWithCustomFields> moduleBaseRes = returnObj.get(BulkAllocationTypes.RESOURCE);
			
			List<ResourceContext> resources = new ArrayList<>();
			
			for(ModuleBaseWithCustomFields moduleBaseRe : moduleBaseRes) {
				resources.add((ResourceContext) moduleBaseRe);
			}
			
			return resources;
		}
		return null;
	}
	
	public static Map<BulkAllocationTypes,List<ModuleBaseWithCustomFields>> getMultipleObjFromBulkConfig(PMAssignmentType pmAssignmentType, List<Long> resourceIds,Long spaceCategoryID,Long assetCategoryID,Long currentAssetId, List<PMIncludeExcludeResourceContext> includeExcludeRess, boolean isMultiSite,Long meterTypeId,List<Long> meterIds) throws Exception {
		
		Map<BulkAllocationTypes,List<ModuleBaseWithCustomFields>> returnValue = new HashMap<>();
		
		List<Long> includedIds = null;
		List<Long> excludedIds = null;
		if(includeExcludeRess != null && !includeExcludeRess.isEmpty()) {
			for(PMIncludeExcludeResourceContext includeExcludeRes :includeExcludeRess) {
				if(includeExcludeRes.getIsInclude()) {
					includedIds = includedIds == null ? new ArrayList<>() : includedIds; 
					includedIds.add(includeExcludeRes.getResourceId());
				}
				else {
					excludedIds = excludedIds == null ? new ArrayList<>() : excludedIds;
					excludedIds.add(includeExcludeRes.getResourceId());
				}
			}
		}
		if(includedIds != null) {
			if(excludedIds != null) {
				includedIds.removeAll(excludedIds);
			}
			if (!isMultiSite) {
				
				List<ResourceContext> includeResources = ResourceAPI.getResources(includedIds, false);
				returnValue.put(BulkAllocationTypes.RESOURCE, getmoduleBaseFromResource(includeResources));
				return returnValue;
			}
		}
		
		List<ResourceContext> selectedResourceContexts = new ArrayList<>();
		List<V3MeterContext> selectedMeterContexts = new ArrayList<>();

		if(resourceIds != null) {
		for (Long resourceId: resourceIds) {
			switch(pmAssignmentType) {
				case ALL_SITES:
					selectedResourceContexts.add(ResourceAPI.getResource(resourceId));
//					if(resourceId == null) {
//						List<SiteContext> sites = SpaceAPI.getAllSites();
//						for (SiteContext site: sites) {
//							selectedResourceContexts.add(site);
//						}
//					}
//					else {
//						selectedResourceContexts.add(ResourceAPI.getResource(resourceId));
//					}
					break;
				case ALL_BUILDINGS:
					List<BaseSpaceContext> siteBuildingsWithFloors = SpaceAPI.getSiteBuildingsWithFloors(resourceId);
					for (BaseSpaceContext building: siteBuildingsWithFloors) {
						selectedResourceContexts.add(building);
					}
					break;
				case ALL_FLOORS:
					List<BaseSpaceContext> floors = SpaceAPI.getBuildingFloors(resourceId);
					for(BaseSpaceContext floor :floors) {
						selectedResourceContexts.add(floor);
					}
					break;
				case ALL_SPACES:
					selectedResourceContexts.addAll(SpaceAPI.getSpaceListForBuilding(resourceId));
					break;
				case SPACE_CATEGORY:
					List<SpaceContext> spaces = SpaceAPI.getSpaceListOfCategory(resourceId, spaceCategoryID);
					selectedResourceContexts.addAll(spaces);
					break;
				case ASSET_CATEGORY:
					List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryID, resourceId);

						selectedResourceContexts.addAll(assets);
						break;
					case CURRENT_ASSET:
						selectedResourceContexts.add(ResourceAPI.getResource(resourceId));
						break;
					case SPECIFIC_ASSET:
						selectedResourceContexts.add(ResourceAPI.getResource(currentAssetId));
						break;
					case METER_TYPE:
						List<V3MeterContext> meters = MeterUtil.getMeterListOfType(meterTypeId, resourceId);
						selectedMeterContexts.addAll(meters);

					default:
						break;
				}
			}
		}
		else if(meterIds != null) {
			for (Long meterId: meterIds) {
				switch(pmAssignmentType) {
					
				case CURRENT_ASSET:
					selectedMeterContexts.add(MeterUtil.getMeter(meterId, false));
					break;
				default:
					break;
				}
			}
		}

		if(excludedIds != null) {
			List<ResourceContext> tempResourceContext = new ArrayList<ResourceContext>();
			
			for(ResourceContext resourceContext : selectedResourceContexts) {
				
				if(!excludedIds.contains(resourceContext.getId())) {
					tempResourceContext.add(resourceContext);
				}
			}
			selectedResourceContexts = tempResourceContext;
		}
		
		List<ResourceContext> commonresourceIds = new ArrayList<>();
		
		if (isMultiSite && includedIds != null) {
			for (int i = 0; i < selectedResourceContexts.size(); i++) {
				for (int j = 0; j < includedIds.size(); j++) {
					if (((Long)selectedResourceContexts.get(i).getId()).equals(includedIds.get(j))) {
						commonresourceIds.add(selectedResourceContexts.get(i));
					}
				}
			}
		} else {
			commonresourceIds = selectedResourceContexts;
		}
		
		returnValue.put(BulkAllocationTypes.RESOURCE, getmoduleBaseFromResource(commonresourceIds));
		returnValue.put(BulkAllocationTypes.METER, getModuleBaseFromMeter(selectedMeterContexts));

		return returnValue;
 	}
	
	public static JSONObject getMultipleResourceCriteriaFromConfig(PMAssignmentType pmAssignmentType,Long siteId,Long baseSpaceId,Long spaceCategoryID,Long assetCategoryID,Long meterTypeId) throws Exception{

		List<Long> resIds= null;
		if(siteId != null) {
			resIds = Collections.singletonList(siteId);
		}

		return getMultipleResourceCriteriaFromConfig(pmAssignmentType, resIds, Collections.singletonList(baseSpaceId), spaceCategoryID, assetCategoryID,meterTypeId);
	}
	
	public static JSONObject getMultipleResourceCriteriaFromConfig(PMAssignmentType pmAssignmentType,List<Long> siteIds,List<Long> baseSpaceIds,Long spaceCategoryID,Long assetCategoryID,Long meterTypeId) throws Exception{
		JSONObject returnObject = new JSONObject();
		Criteria criteria = new Criteria();
		switch (pmAssignmentType) {
			case ALL_SITES:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.SITE);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getIdCondition(siteIds, Constants.getModBean().getModule(FacilioConstants.ContextNames.SITE)));
					returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				}
				else {
					returnObject.put(FacilioConstants.ContextNames.CRITERIA, null);
				}
				break;
			case ALL_BUILDINGS:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.BUILDING);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID", "site", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				break;
			case ALL_FLOORS:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.FLOOR);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID", "site", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				if (!CollectionUtils.isEmpty(baseSpaceIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("BUILDING_ID", "building", StringUtils.join(baseSpaceIds, ","), PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				break;
			case ALL_SPACES:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.SPACE);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID", "site", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				if (!CollectionUtils.isEmpty(baseSpaceIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("BUILDING_ID", "building", StringUtils.join(baseSpaceIds, ","), PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				break;
			case SPACE_CATEGORY:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.SPACE);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID", "site", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				if (!CollectionUtils.isEmpty(baseSpaceIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("BUILDING_ID", "building", StringUtils.join(baseSpaceIds, ","), PickListOperators.IS));
				}
				if (spaceCategoryID != null && spaceCategoryID > 0) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SPACE_CATEGORY_ID", "spaceCategory", spaceCategoryID + "", PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				break;
			case ASSET_CATEGORY:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.ASSET);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				if (!CollectionUtils.isEmpty(baseSpaceIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(baseSpaceIds, ","), BuildingOperator.BUILDING_IS));
				}
				if (assetCategoryID != null && assetCategoryID > 0) {
					criteria.addAndCondition(CriteriaAPI.getCondition("CATEGORY", "category", assetCategoryID + "", PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				break;
			case CURRENT_ASSET:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.ContextNames.BASE_SPACE);
				if (!CollectionUtils.isEmpty(baseSpaceIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(baseSpaceIds, ","), PickListOperators.IS));
				} else if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
				break;
			case METER_TYPE:
				returnObject.put(FacilioConstants.ContextNames.MODULE, FacilioConstants.Meter.METER);
				if (!CollectionUtils.isEmpty(siteIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", StringUtils.join(siteIds, ","), PickListOperators.IS));
				}
				if (!CollectionUtils.isEmpty(baseSpaceIds)) {
					criteria.addAndCondition(CriteriaAPI.getCondition("METER_LOCATION_ID", "meterLocation", StringUtils.join(baseSpaceIds, ","), BuildingOperator.BUILDING_IS));
				}
				if (meterTypeId != null && meterTypeId > 0) {
					criteria.addAndCondition(CriteriaAPI.getCondition("UTILITY_TYPE", "utilityType", meterTypeId + "", PickListOperators.IS));
				}
				returnObject.put(FacilioConstants.ContextNames.CRITERIA, criteria);
			default:
				break;
		}
		return returnObject;
	}
	
	public static List<ModuleBaseWithCustomFields> getmoduleBaseFromResource(List<ResourceContext> resources) {
		
		if(resources != null && !resources.isEmpty()) {
			List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = new ArrayList<>();
			for(ResourceContext resource : resources) {
				moduleBaseWithCustomFields.add(resource);
			}
			return moduleBaseWithCustomFields;
		}
		return null;
	}
	
	public static List<ModuleBaseWithCustomFields> getModuleBaseFromMeter(List<V3MeterContext> meterContext) {
		
		if(meterContext != null && !meterContext.isEmpty()) {
			List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = new ArrayList<>();
			for(V3MeterContext resource : meterContext) {
				moduleBaseWithCustomFields.add(resource);
			}
			return moduleBaseWithCustomFields;
		}
		return null;
	}
}
