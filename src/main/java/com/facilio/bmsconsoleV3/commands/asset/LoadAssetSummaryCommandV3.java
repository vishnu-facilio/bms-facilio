package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class LoadAssetSummaryCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        List<ModuleBaseWithCustomFields> recordList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(assetList)){

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(assetList.get(0).getCategory().getId());
            long assetModuleID = assetCategory.getAssetModuleID();
            FacilioModule module = modBean.getModule(assetModuleID);
            String moduleName = module.getName();

            List<FacilioField> fields = modBean.getAllFields(moduleName);

            V3Config v3Config = ChainUtil.getV3Config(moduleName);
            Class beanClassName = ChainUtil.getBeanClass(v3Config, module);
            if (beanClassName == null) {
                beanClassName = ModuleBaseWithCustomFields.class;
            }

            List<SupplementRecord> supplementFields = new ArrayList<>();

            Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

            SupplementRecord identifiedLocationField = (SupplementRecord) fieldsAsMap.get("identifiedLocation");
            SupplementRecord spaceField = (SupplementRecord) fieldsAsMap.get("space");
            SupplementRecord moduleStateField = (SupplementRecord) fieldsAsMap.get("moduleState");
            SupplementRecord categoryField = (SupplementRecord) fieldsAsMap.get("category");

            supplementFields.add(identifiedLocationField);
            supplementFields.add(spaceField);
            supplementFields.add(moduleStateField);
            supplementFields.add(categoryField);

            LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
            supplementFields.add(sysCreatedBy);
            LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
            supplementFields.add(sysModifiedBy);

            for(V3AssetContext asset :assetList){
                SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                        .module(module)
                        .beanClass(beanClassName)
                        .select(fields)
                        .andCondition(CriteriaAPI.getIdCondition(asset.getId(), module))
                        ;

                if (CollectionUtils.isNotEmpty(supplementFields)) {
                    builder.fetchSupplements(supplementFields);
                }

                List<ModuleBaseWithCustomFields> records = builder.get();
                if(records.size() > 0) {
                    ResourceAPI.loadModuleResources(records, fields);
                    V3AssetContext assetRec = (V3AssetContext) records.get(0);
                    assetRec.setCategoryModuleName(moduleName);
                    this.getAssetLocation(assetRec);
                    recordList.add(assetRec);
                }
            }

            Map<String,Object> recordMap = new HashMap<String, Object>();

            recordMap.put("asset", recordList);

            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);

        }

        return false;
    }

    private void getAssetLocation(V3AssetContext asset) throws Exception {
        V3BaseSpaceContext assetLocation = asset.getSpace();
        if (assetLocation == null || assetLocation.getId() <= 0) {
            return;
        }
        Set<Long> spaceIds = new HashSet<>();
        LocationContext currentLocation = null;
        if (assetLocation.getSiteId() > 0) {
            if (asset.getCurrentLocation() == null) {
                V3SiteContext assetSite = V3SpaceAPI.getSiteSpace(assetLocation.getSiteId());
                V3SiteContext site = new V3SiteContext();
                site.setId(assetSite.getSiteId());
                site.setName(assetSite.getName());
                assetLocation.setSite(site);
                if (assetSite.getLocation() != null) {
                    currentLocation = assetSite.getLocation();
                }
            }
            else {
                if (assetLocation.getSiteId() == assetLocation.getId()) {
                    V3SiteContext site = new V3SiteContext();
                    site.setId(assetLocation.getSiteId());
                    site.setName(assetLocation.getName());
                    assetLocation.setSite(site);
                }
                else {
                    spaceIds.add(assetLocation.getSiteId());
                }
            }
        }
        if (assetLocation.getBuildingId() != null && assetLocation.getBuildingId() > 0) {
            if (asset.getCurrentLocation() == null && currentLocation == null) {
                V3BuildingContext assetBuilding = V3SpaceAPI.getBuildingSpace(assetLocation.getBuildingId(), true);
                V3BuildingContext building = new V3BuildingContext();
                building.setId(assetBuilding.getBuildingId());
                building.setName(assetBuilding.getName());
                assetLocation.setBuilding(building);
                if (assetBuilding.getLocation() != null) {
                    currentLocation = assetBuilding.getLocation();
                }
            }
            else {
                if (assetLocation.getBuildingId() == assetLocation.getId()) {
                    V3BuildingContext building = new V3BuildingContext();
                    building.setId(assetLocation.getBuildingId());
                    building.setName(assetLocation.getName());
                    assetLocation.setBuilding(building);
                }
                else {
                    spaceIds.add(assetLocation.getBuildingId());
                }
            }
        }
        if (assetLocation.getFloorId() != null && assetLocation.getFloorId() > 0) {
            if (assetLocation.getFloorId() == assetLocation.getId()) {
                V3FloorContext floor = new V3FloorContext();
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
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId());
                space.setName(assetLocation.getName());
                assetLocation.setSpace(space);
            }
            else {
                spaceIds.add(assetLocation.getSpaceId());
            }
        }
        if (assetLocation.getSpaceId1() != null && assetLocation.getSpaceId1() > 0) {
            if (assetLocation.getSpaceId1() == assetLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId1());
                space.setName(assetLocation.getName());
                assetLocation.setSpace1(space);
            }
            else {
                spaceIds.add(assetLocation.getSpaceId1());
            }
        }
        if (assetLocation.getSpaceId2() != null && assetLocation.getSpaceId2() > 0) {
            if (assetLocation.getSpaceId2() == assetLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId2());
                space.setName(assetLocation.getName());
                assetLocation.setSpace2(space);
            }
            else {
                spaceIds.add(assetLocation.getSpaceId2());
            }
        }
        if (assetLocation.getSpaceId3() != null && assetLocation.getSpaceId3() > 0) {
            if (assetLocation.getSpaceId3() == assetLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId3());
                space.setName(assetLocation.getName());
                assetLocation.setSpace3(space);
            }
            else {
                spaceIds.add(assetLocation.getSpaceId3());
            }
        }
        if (assetLocation.getSpaceId4() != null && assetLocation.getSpaceId4() > 0) {
            if (assetLocation.getSpaceId4() == assetLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
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
                V3SiteContext site = new V3SiteContext();
                site.setId(assetLocation.getSiteId());
                site.setName(spaceMap.get(assetLocation.getSiteId()).getName());
                assetLocation.setSite(site);
            }
            if (spaceMap.containsKey(assetLocation.getBuildingId())) {
                V3BuildingContext building = new V3BuildingContext();
                building.setId(assetLocation.getBuildingId());
                building.setName(spaceMap.get(assetLocation.getBuildingId()).getName());
                assetLocation.setBuilding(building);
            }
            if (spaceMap.containsKey(assetLocation.getFloorId())) {
                V3FloorContext floor = new V3FloorContext();
                floor.setId(assetLocation.getFloorId());
                floor.setName(spaceMap.get(assetLocation.getFloorId()).getName());
                assetLocation.setFloor(floor);
            }
            if (spaceMap.containsKey(assetLocation.getSpaceId())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId());
                space.setName(spaceMap.get(assetLocation.getSpaceId()).getName());
                assetLocation.setSpace(space);
            }
            if (spaceMap.containsKey(assetLocation.getSpaceId1())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId1());
                space.setName(spaceMap.get(assetLocation.getSpaceId1()).getName());
                assetLocation.setSpace1(space);
            }
            if (spaceMap.containsKey(assetLocation.getSpaceId2())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId2());
                space.setName(spaceMap.get(assetLocation.getSpaceId2()).getName());
                assetLocation.setSpace2(space);
            }
            if (spaceMap.containsKey(assetLocation.getSpaceId3())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(assetLocation.getSpaceId3());
                space.setName(spaceMap.get(assetLocation.getSpaceId3()).getName());
                assetLocation.setSpace3(space);
            }
            if (spaceMap.containsKey(assetLocation.getSpaceId4())) {
                V3SpaceContext space = new V3SpaceContext();
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
