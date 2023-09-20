package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoadMeterDetailsCommand  extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(LoadMeterDetailsCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3MeterContext> meterList = (List<V3MeterContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("meter"));
        List<ModuleBaseWithCustomFields> recordList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(meterList)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            HashMap<Long, ArrayList<Long>> meterCat = new HashMap<>();
            for (V3MeterContext meter : meterList) {
                Long catId = meter.getUtilityType().getId();
                if (meterCat.containsKey(catId)) {
                    ArrayList<Long> list = meterCat.get(catId);
                    list.add(meter.getId());
                    meterCat.put(catId, list);
                } else {
                    ArrayList<Long> list = new ArrayList<Long>();
                    list.add(meter.getId());
                    meterCat.put(catId, list);
                }
            }

            for (Map.Entry<Long, ArrayList<Long>> entry : meterCat.entrySet()) {
                V3UtilityTypeContext utilityType = MetersAPI.getUtilityTypeForMeter(entry.getKey());
                long meterModuleID = utilityType.getMeterModuleID();
                FacilioModule module = modBean.getModule(meterModuleID);
                String moduleName = module.getName();
                List<FacilioField> fields = modBean.getAllFields(moduleName);

                V3Config v3Config = ChainUtil.getV3Config(moduleName);
                Class beanClassName = ChainUtil.getBeanClass(v3Config, module);
                if (beanClassName == null) {
                    beanClassName = ModuleBaseWithCustomFields.class;
                }

                List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);

                    SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                            .module(module)
                            .beanClass(beanClassName)
                            .select(fields);
                if(entry.getValue().size() > 1){
                    builder.andCondition(CriteriaAPI.getIdCondition(entry.getValue(), module));
                }
                else{
                    builder.andCondition(CriteriaAPI.getIdCondition(entry.getValue().get(0), module));
                }
                    if (CollectionUtils.isNotEmpty(supplementFields)) {
                        builder.fetchSupplements(supplementFields);
                    }

                    List<ModuleBaseWithCustomFields> records = builder.get();
                    if (records.size() > 0) {
                        ResourceAPI.loadModuleResources(records, fields);
                        for(ModuleBaseWithCustomFields meterContext : records){
                            V3MeterContext meterRec = (V3MeterContext) meterContext;
                            // set Virtual Meter Template Name in meter Context
                            if(meterRec.getVirtualMeterTemplate() != null) {
                                Long vmTemplateId = meterRec.getVirtualMeterTemplate().getId();
                                if(vmTemplateId != null) {
                                    VirtualMeterTemplateContext vmTemplate = MetersAPI.getVirtualMeterTemplateForMeter(vmTemplateId);
                                    VirtualMeterTemplateContext vmTemplateContext = new VirtualMeterTemplateContext();
                                    vmTemplateContext.setId(vmTemplate.getId());
                                    vmTemplateContext.setName(vmTemplate.getName());
                                    meterRec.setVirtualMeterTemplate(vmTemplateContext);
                                }
                            }

                            LOGGER.info("Meter Summary Record ===>" + meterRec);
                            meterRec.setUtilityTypeModuleName(moduleName);
                            this.getMeterLocation(meterRec);
                            recordList.add(meterRec);
                        }
                    }
        }
            Map<String,Object> recordMap = new HashMap<String, Object>();

            recordMap.put("meter", recordList);

            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);

        }

        return false;
    }

    private void getMeterLocation(V3MeterContext meter) throws Exception {
        V3BaseSpaceContext meterLocation = meter.getMeterLocation();

        if (meterLocation == null || meterLocation.getId() <= 0) {
            return;
        }

        LOGGER.debug("Meter Summary location ID ===>"+meterLocation.getId());
        LOGGER.debug("Meter Summary Site ID ===>"+meterLocation.getSiteId() + " -- "+meterLocation.getSite());
        LOGGER.debug("Meter Summary Building ID ===>"+meterLocation.getBuildingId() + " -- "+meterLocation.getBuilding());
        LOGGER.debug("Meter Summary Floor ID ===>"+meterLocation.getFloorId() + " -- "+meterLocation.getFloor());
        LOGGER.debug("Meter Summary Space ID ===>"+meterLocation.getSpaceId() + " -- "+meterLocation.getSpace());

        Set<Long> spaceIds = new HashSet<>();
        LocationContext currentLocation = null;
        if (meterLocation.getSiteId() > 0) {
                V3SiteContext meterSite = V3SpaceAPI.getSiteSpace(meterLocation.getSiteId());
                if(meterSite != null) {
                	
                	V3SiteContext site = new V3SiteContext();
                    site.setId(meterSite.getId());
                    site.setName(meterSite.getName());
                    meterLocation.setSite(site);
                    if (meterSite.getLocation() != null) {
                        currentLocation = meterSite.getLocation();
                    }
                }
        }
        if (meterLocation.getBuildingId() != null && meterLocation.getBuildingId() > 0) {
            if (currentLocation == null) {
                V3BuildingContext meterBuilding = V3SpaceAPI.getBuildingSpace(meterLocation.getBuildingId());
                if(meterBuilding != null) {
                	V3BuildingContext building = new V3BuildingContext();
                    building.setId(meterBuilding.getId());
                    building.setName(meterBuilding.getName());
                    meterLocation.setBuilding(building);
                }
            }
            else {
                if (meterLocation.getBuildingId() == meterLocation.getId()) {
                    V3BuildingContext building = new V3BuildingContext();
                    building.setId(meterLocation.getBuildingId());
                    building.setName(meterLocation.getName());
                    meterLocation.setBuilding(building);
                }
                else {
                    spaceIds.add(meterLocation.getBuildingId());
                }
            }
        }
        if (meterLocation.getFloorId() != null && meterLocation.getFloorId() > 0) {
            if (meterLocation.getFloorId() == meterLocation.getId()) {
                V3FloorContext floor = new V3FloorContext();
                floor.setId(meterLocation.getFloorId());
                floor.setName(meterLocation.getName());
                meterLocation.setFloor(floor);
            }
            else {
                spaceIds.add(meterLocation.getFloorId());
            }
        }
        if (meterLocation.getSpaceId() > 0) {
            if (meterLocation.getSpaceId() == meterLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId());
                space.setName(meterLocation.getName());
                meterLocation.setSpace(space);
            }
            else {
                spaceIds.add(meterLocation.getSpaceId());
            }
        }
        if (meterLocation.getSpaceId1() != null && meterLocation.getSpaceId1() > 0) {
            if (meterLocation.getSpaceId1() == meterLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId1());
                space.setName(meterLocation.getName());
                meterLocation.setSpace1(space);
            }
            else {
                spaceIds.add(meterLocation.getSpaceId1());
            }
        }
        if (meterLocation.getSpaceId2() != null && meterLocation.getSpaceId2() > 0) {
            if (meterLocation.getSpaceId2() == meterLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId2());
                space.setName(meterLocation.getName());
                meterLocation.setSpace2(space);
            }
            else {
                spaceIds.add(meterLocation.getSpaceId2());
            }
        }
        if (meterLocation.getSpaceId3() != null && meterLocation.getSpaceId3() > 0) {
            if (meterLocation.getSpaceId3() == meterLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId3());
                space.setName(meterLocation.getName());
                meterLocation.setSpace3(space);
            }
            else {
                spaceIds.add(meterLocation.getSpaceId3());
            }
        }
        if (meterLocation.getSpaceId4() != null && meterLocation.getSpaceId4() > 0) {
            if (meterLocation.getSpaceId4() == meterLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId4());
                space.setName(meterLocation.getName());
                meterLocation.setSpace4(space);
            }
            else {
                spaceIds.add(meterLocation.getSpaceId4());
            }
        }
        if (meterLocation.getSpaceId5() != null && meterLocation.getSpaceId5() > 0){
            if (meterLocation.getSpaceId5() == meterLocation.getId()) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId5());
                space.setName(meterLocation.getName());
                meterLocation.setSpace5(space);
            }
            else {
                spaceIds.add(meterLocation.getSpaceId5());
            }
        }
        if (CollectionUtils.isNotEmpty(spaceIds)) {
            Map<Long, BaseSpaceContext> spaceMap = SpaceAPI.getBaseSpaceMap(spaceIds);
            if (spaceMap.containsKey(meterLocation.getSiteId())) {
                V3SiteContext site = new V3SiteContext();
                site.setId(meterLocation.getSiteId());
                site.setName(spaceMap.get(meterLocation.getSiteId()).getName());
                meterLocation.setSite(site);
            }
            if (spaceMap.containsKey(meterLocation.getBuildingId())) {
                V3BuildingContext building = new V3BuildingContext();
                building.setId(meterLocation.getBuildingId());
                building.setName(spaceMap.get(meterLocation.getBuildingId()).getName());
                meterLocation.setBuilding(building);
            }
            if (spaceMap.containsKey(meterLocation.getFloorId())) {
                V3FloorContext floor = new V3FloorContext();
                floor.setId(meterLocation.getFloorId());
                floor.setName(spaceMap.get(meterLocation.getFloorId()).getName());
                meterLocation.setFloor(floor);
            }
            if (spaceMap.containsKey(meterLocation.getSpaceId())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId());
                space.setName(spaceMap.get(meterLocation.getSpaceId()).getName());
                meterLocation.setSpace(space);
            }
            if (spaceMap.containsKey(meterLocation.getSpaceId1())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId1());
                space.setName(spaceMap.get(meterLocation.getSpaceId1()).getName());
                meterLocation.setSpace1(space);
            }
            if (spaceMap.containsKey(meterLocation.getSpaceId2())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId2());
                space.setName(spaceMap.get(meterLocation.getSpaceId2()).getName());
                meterLocation.setSpace2(space);
            }
            if (spaceMap.containsKey(meterLocation.getSpaceId3())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId3());
                space.setName(spaceMap.get(meterLocation.getSpaceId3()).getName());
                meterLocation.setSpace3(space);
            }
            if (spaceMap.containsKey(meterLocation.getSpaceId4())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId4());
                space.setName(spaceMap.get(meterLocation.getSpaceId4()).getName());
                meterLocation.setSpace4(space);
            }
            if (spaceMap.containsKey(meterLocation.getSpaceId5())) {
                V3SpaceContext space = new V3SpaceContext();
                space.setId(meterLocation.getSpaceId5());
                space.setName(spaceMap.get(meterLocation.getSpaceId5()).getName());
                meterLocation.setSpace5(space);
            }
        }
    }


}
