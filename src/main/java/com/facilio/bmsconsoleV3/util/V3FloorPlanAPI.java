package com.facilio.bmsconsoleV3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class V3FloorPlanAPI {

    public static List<V3MarkerContext> getUpdateMarkerList(List<V3MarkerContext> newMarkers, List<V3MarkerContext> oldMarkers) throws Exception {
        
        List<V3MarkerContext> updateMarkers = new ArrayList<>();

         for (V3MarkerContext marker: newMarkers) {
             if (marker.getId() > 0) {
                 updateMarkers.add(marker);
             }
         }
        
        if (CollectionUtils.isNotEmpty(updateMarkers)) {
            return updateMarkers;
        }
       return null;
    }

     public static List<V3MarkerdZonesContext> getUpdateZonesList(List<V3MarkerdZonesContext> newZones, List<V3MarkerdZonesContext> oldZones) throws Exception {
        
        List<V3MarkerdZonesContext> updateZones = new ArrayList<>();

         for (V3MarkerdZonesContext zone: newZones) {
             if (zone.getId() > 0) {
                 updateZones.add(zone);
             }
         }
        
        if (CollectionUtils.isNotEmpty(updateZones)) {
            return updateZones;
        }
       return null;
    }

     public static List<V3MarkerContext> getAddMarkerList(List<V3MarkerContext> newMarkers, List<V3MarkerContext> oldMarkers) throws Exception {
        
        List<V3MarkerContext> addMarkers = new ArrayList<>();

         for (V3MarkerContext marker: newMarkers) {
             if (marker.getId() < 0) {
                 addMarkers.add(marker);
             }
         }
        
        if (CollectionUtils.isNotEmpty(addMarkers)) {
            return addMarkers;
        }
       return null;
    }

     public static List<V3MarkerdZonesContext> getAddZonesList(List<V3MarkerdZonesContext> newZones, List<V3MarkerdZonesContext> oldZones) throws Exception {
        
        List<V3MarkerdZonesContext> zones = new ArrayList<>();

         for (V3MarkerdZonesContext zone: newZones) {
             if (zone.getId() < 0) {
                 zones.add(zone);
             }
         }
        
        if (CollectionUtils.isNotEmpty(zones)) {
            return zones;
        }
       return null;
    }

    public static List<V3MarkerdZonesContext> getAllZones(Long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<V3MarkerdZonesContext> builder = new SelectRecordsBuilder<V3MarkerdZonesContext>()
                .module(module)
                .beanClass(V3MarkerdZonesContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition("FLOORPLAN_ID", "floorplanId",
						String.valueOf(parentId), NumberOperators.EQUALS));
        
        List<V3MarkerdZonesContext> zones = builder.get();
        if(CollectionUtils.isNotEmpty(zones))
        {
            return zones;
        }
        return null;
    }
    public static List<V3MarkerContext> getAllMarkers(Long parentId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<V3MarkerContext> builder = new SelectRecordsBuilder<V3MarkerContext>()
                .module(module)
                .beanClass(V3MarkerContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition("FLOORPLAN_ID", "floorplanId",
						String.valueOf(parentId), NumberOperators.EQUALS));
        
        List<V3MarkerContext> markers = builder.get();
        if(CollectionUtils.isNotEmpty(markers))
        {
            return markers;
        }
        return null;
    }

    public static List<V3MarkerContext> addMarkers(V3IndoorFloorPlanContext floorplan) throws Exception {
        List<V3IndoorFloorPlanContext> floorplans = new ArrayList<V3IndoorFloorPlanContext>();
     
		Map<String,Object> recordMap = new HashMap<String, Object>();
		floorplans.add(floorplan);
        recordMap.put((String)FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, floorplans);
        FacilioChain chain = TransactionChainFactoryV3.addMarkersAndModulesCommand();
        chain.getContext().put(Constants.RECORD_MAP, recordMap);
        chain.execute();
        
        return null;
    }

     public static List<V3MarkerdZonesContext> addZones(V3IndoorFloorPlanContext floorplan) throws Exception {
     
				List<V3MarkerdZonesContext> zones = floorplan.getMarkedZones();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
		        List<FacilioField> fields = modBean.getAllFields(module.getName());

				if (CollectionUtils.isNotEmpty(zones)) {

					for (V3MarkerdZonesContext zone : zones) {
						Map<String, Object> floorObject = new HashMap<>();
						floorObject.put("id", floorplan.getId());
						zone.setIndoorfloorplan(FieldUtil.getAsBeanFromMap(floorObject, V3IndoorFloorPlanContext.class));
					}
					Map<Long, List<UpdateChangeSet>> zoneChangeSet = V3RecordAPI.addRecord(false, zones, module, fields, true);
					if(zoneChangeSet != null && !zoneChangeSet.isEmpty()) {
						for(Long zoneId : zoneChangeSet.keySet()) {
							V3MarkerdZonesContext changedZone = (V3MarkerdZonesContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES, zoneId, V3MarkerdZonesContext.class);
							
							DesksAPI.AddorDeleteFacilityForSpace(changedZone);
						}
						}
				}
        
        return null;
    }


    public static List<V3MarkerContext> updateMarkers(List<V3MarkerContext> markers) throws Exception {
        
		Map<String,Object> recordMap = new HashMap<String, Object>();
        recordMap.put((String)FacilioConstants.ContextNames.Floorplan.MARKER, markers);
        FacilioChain chain = TransactionChainFactoryV3.updateMarkersAndModulesCommand();
        chain.getContext().put(Constants.RECORD_MAP, recordMap);
        chain.execute();
        
       return null;
    }

     public static List<V3MarkerdZonesContext> updateZones(V3IndoorFloorPlanContext floorplan) throws Exception {
        
			List<V3MarkerdZonesContext> zones = floorplan.getMarkedZones();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
		        List<FacilioField> fields = modBean.getAllFields(module.getName());

				if (CollectionUtils.isNotEmpty(zones)) {

					for (V3MarkerdZonesContext zone : zones) {
						Map<String, Object> floorObject = new HashMap<>();
						floorObject.put("id", floorplan.getId());
						zone.setIndoorfloorplan(FieldUtil.getAsBeanFromMap(floorObject, V3IndoorFloorPlanContext.class));
						Map<Long, List<UpdateChangeSet>> zoneChangeSet = V3RecordAPI.updateRecord(zone, module, fields, true);
                        if(zoneChangeSet != null && !zoneChangeSet.isEmpty()) {
        					for(Long zoneId : zoneChangeSet.keySet()) {
        						V3MarkerdZonesContext changedzone = (V3MarkerdZonesContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES, zoneId, V3MarkerdZonesContext.class);
        						
        						DesksAPI.AddorDeleteFacilityForSpace(changedzone);
        					}
                        }
					}
				}
        
       return null;
    }

}
