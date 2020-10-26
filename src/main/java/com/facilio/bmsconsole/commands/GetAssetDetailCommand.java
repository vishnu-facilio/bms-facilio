package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetAssetDetailCommand extends GenericGetModuleDataDetailCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetAssetDetailCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {

		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			super.executeCommand(context);

			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			AssetContext assetContext = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (assetContext != null && assetContext.getId() > 0) {
				AssetsAPI.loadAssetsLookups(Collections.singletonList(assetContext));

				Boolean fetchHierarchy = (Boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_HIERARCHY, false);
				if (fetchHierarchy || AccountUtil.getCurrentAccount().isFromMobile()) {
					fetchHierarchy(assetContext);
				}
			}

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (assetContext.getModuleId() != -1) {
				FacilioModule module = modBean.getModule(assetContext.getModuleId());
				if (module != null) {
					assetContext.setModuleName(module.getName());
				}
				else {
					assetContext.setModuleName(FacilioConstants.ContextNames.ASSET);
					LOGGER.info("No module for asset" + assetContext.getId() + ", moduleId:" + assetContext.getModuleId());
				}
			}
			else {
				assetContext.setModuleName(FacilioConstants.ContextNames.ASSET);
				LOGGER.info("No module ID for asset" + assetContext.getId());
			}
			context.put(FacilioConstants.ContextNames.ASSET, assetContext);
		}
		return false;
	}

	private void fetchHierarchy(AssetContext asset) throws Exception {
		BaseSpaceContext assetLocation = asset.getSpace();
		if (assetLocation == null || assetLocation.getId() <= 0) {
			return;
		}
		Set<Long> spaceIds = new HashSet<>();
		LocationContext currentLocation = null;
		if (assetLocation.getSiteId() > 0) {
			if (asset.getCurrentLocation() == null) {
				SiteContext assetSite = SpaceAPI.getSiteSpace(assetLocation.getSiteId());
				SiteContext site = new SiteContext();
				site.setId(assetSite.getSiteId());
				site.setName(assetSite.getName());
				assetLocation.setSite(site);
				if (assetSite.getLocation() != null) {
					currentLocation = assetSite.getLocation();
				}
			}
			else {
				if (assetLocation.getSiteId() == assetLocation.getId()) {
					SiteContext site = new SiteContext();
					site.setId(assetLocation.getSiteId());
					site.setName(assetLocation.getName());
					assetLocation.setSite(site);
				}
				else {
					spaceIds.add(assetLocation.getSiteId());
				}
			}
		}
		if (assetLocation.getBuildingId() > 0) {
			if (asset.getCurrentLocation() == null && currentLocation == null) {
				BuildingContext assetBuilding = SpaceAPI.getBuildingSpace(assetLocation.getBuildingId());
				BuildingContext building = new BuildingContext();
				building.setId(assetBuilding.getBuildingId());
				building.setName(assetBuilding.getName());
				assetLocation.setBuilding(building);
				if (assetBuilding.getLocation() != null) {
					currentLocation = assetBuilding.getLocation();
				}
			}
			else {
				if (assetLocation.getBuildingId() == assetLocation.getId()) {
					BuildingContext building = new BuildingContext();
					building.setId(assetLocation.getBuildingId());
					building.setName(assetLocation.getName());
					assetLocation.setBuilding(building);
				}
				else {
					spaceIds.add(assetLocation.getBuildingId());
				}
			}
		}
		if (assetLocation.getFloorId() > 0) {
			if (assetLocation.getFloorId() == assetLocation.getId()) {
				FloorContext floor = new FloorContext();
				floor.setId(assetLocation.getFloorId());
				floor.setName(assetLocation.getName());
				assetLocation.setFloor(floor);
			}
			else {
				spaceIds.add(assetLocation.getFloorId());
			}
		}
		if (assetLocation.getSpaceId() > 0) {
			if (assetLocation.getSpaceId() == assetLocation.getId()) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId());
				space.setName(assetLocation.getName());
				assetLocation.setSpace(space);
			}
			else {
				spaceIds.add(assetLocation.getSpaceId());
			}
		}
		if (assetLocation.getSpaceId1() > 0) {
			if (assetLocation.getSpaceId1() == assetLocation.getId()) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId1());
				space.setName(assetLocation.getName());
				assetLocation.setSpace1(space);
			}
			else {
				spaceIds.add(assetLocation.getSpaceId1());
			}
		}
		if (assetLocation.getSpaceId2() > 0) {
			if (assetLocation.getSpaceId2() == assetLocation.getId()) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId2());
				space.setName(assetLocation.getName());
				assetLocation.setSpace2(space);
			}
			else {
				spaceIds.add(assetLocation.getSpaceId2());
			}
		}
		if (assetLocation.getSpaceId3() > 0) {
			if (assetLocation.getSpaceId3() == assetLocation.getId()) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId3());
				space.setName(assetLocation.getName());
				assetLocation.setSpace3(space);
			}
			else {
				spaceIds.add(assetLocation.getSpaceId3());
			}
		}
		if (assetLocation.getSpaceId4() > 0) {
			if (assetLocation.getSpaceId4() == assetLocation.getId()) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId4());
				space.setName(assetLocation.getName());
				assetLocation.setSpace4(space);
			}
			else {
				spaceIds.add(assetLocation.getSpaceId4());
			}
		}
		if (CollectionUtils.isNotEmpty(spaceIds)) {
			Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(spaceIds);
			if (spaceMap.containsKey(assetLocation.getSiteId())) {
				SiteContext site = new SiteContext();
				site.setId(assetLocation.getSiteId());
				site.setName(spaceMap.get(assetLocation.getSiteId()).getName());
				assetLocation.setSite(site);
			}
			if (spaceMap.containsKey(assetLocation.getBuildingId())) {
				BuildingContext building = new BuildingContext();
				building.setId(assetLocation.getBuildingId());
				building.setName(spaceMap.get(assetLocation.getBuildingId()).getName());
				assetLocation.setBuilding(building);
			}
			if (spaceMap.containsKey(assetLocation.getFloorId())) {
				FloorContext floor = new FloorContext();
				floor.setId(assetLocation.getFloorId());
				floor.setName(spaceMap.get(assetLocation.getFloorId()).getName());
				assetLocation.setFloor(floor);
			}
			if (spaceMap.containsKey(assetLocation.getSpaceId())) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId());
				space.setName(spaceMap.get(assetLocation.getSpaceId()).getName());
				assetLocation.setSpace(space);
			}
			if (spaceMap.containsKey(assetLocation.getSpaceId1())) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId1());
				space.setName(spaceMap.get(assetLocation.getSpaceId1()).getName());
				assetLocation.setSpace1(space);
			}
			if (spaceMap.containsKey(assetLocation.getSpaceId2())) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId2());
				space.setName(spaceMap.get(assetLocation.getSpaceId2()).getName());
				assetLocation.setSpace2(space);
			}
			if (spaceMap.containsKey(assetLocation.getSpaceId3())) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId3());
				space.setName(spaceMap.get(assetLocation.getSpaceId3()).getName());
				assetLocation.setSpace3(space);
			}
			if (spaceMap.containsKey(assetLocation.getSpaceId4())) {
				SpaceContext space = new SpaceContext();
				space.setId(assetLocation.getSpaceId4());
				space.setName(spaceMap.get(assetLocation.getSpaceId4()).getName());
				assetLocation.setSpace4(space);
			}
		}
		if(asset.getCurrentLocation() == null && currentLocation != null) {
			asset.setCurrentLocation(currentLocation.getLat()+","+currentLocation.getLng());
		}
	}
}
