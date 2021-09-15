package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanGeoJsonContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanPropertiesContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

public class getIndoorFloorPlanViewerCommand extends FacilioCommand {

	@SuppressWarnings("null")
	@Override
	public boolean executeCommand(Context context) throws Exception {

	    long floorplanId = (long) context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID);

		String viewMode = (String) context.get(FacilioConstants.ContextNames.Floorplan.VIEW_MODE);
		
		if (viewMode == null) {
			viewMode = FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW;
		}

	    List<Long> objectIds = (List<Long>) context.get(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS);
		
        List<V3MarkerContext> markers = V3FloorPlanAPI.getAllMarkers(floorplanId, objectIds);
        
        List<V3MarkerdZonesContext> zones = V3FloorPlanAPI.getAllZones(floorplanId, objectIds);

        
        List<V3IndoorFloorPlanGeoJsonContext> features =  new ArrayList<V3IndoorFloorPlanGeoJsonContext>(); 
        
        Map<Long, V3IndoorFloorPlanGeoJsonContext> markerObjectMap = new HashMap<Long,V3IndoorFloorPlanGeoJsonContext >();
        Map<Long, V3IndoorFloorPlanGeoJsonContext> zoneObjectMap = new HashMap<Long,V3IndoorFloorPlanGeoJsonContext >();

        
        Map<Long, List<Long>> markerIdvsRecordIds = new HashMap<>();

        Map<String, List<ModuleBaseWithCustomFields>> markerIdvsModuleListData = new HashMap<>();
        
        Map<Long, List<Long>> zoneIdvsRecordIds = new HashMap<>();

        Map<String, List<ModuleBaseWithCustomFields>> zoneIdvsModuleListData = new HashMap<>();
        
        Map<Long, SpaceContext> spacesMap = new HashMap<>();
        
        List<SpaceContext> spaceList = new ArrayList<SpaceContext>(); 

        
        
        Map<Long,  Map<Long, ModuleBaseWithCustomFields>> markervsRecordObjectMap= new HashMap<>();

        Map<Long,  Map<Long, ModuleBaseWithCustomFields>> zonevsRecordObjectMap= new HashMap<>();

		
        
        if (markers != null && markers.size() > 0) {
        	for (V3MarkerContext marker: markers) {
            	marker.setObjectType(1);
            	Long recordId = (Long) marker.getRecordId();
            	Long markerModuleId = (Long) marker.getMarkerModuleId();
            	
            	if (recordId != null && markerModuleId != null ) {
            		
            		List<Long> recordIds = markerIdvsRecordIds.get(markerModuleId) == null ? new ArrayList<>() : markerIdvsRecordIds.get(markerModuleId);
         		
            		if (recordIds != null) {
            			recordIds.add(recordId);
                		markerIdvsRecordIds.put(markerModuleId, recordIds);

            		}
            		else {
                		markerIdvsRecordIds.put(markerModuleId, new ArrayList<>());
                		recordIds.add(recordId);
                		markerIdvsRecordIds.put(markerModuleId, recordIds);
            		}
            	}
            	
            }
        }
        
		
		
		if (zones != null && zones.size() > 0) {
			
			List<Long> zoneRecordIds = new ArrayList<>();

			
			for (V3MarkerdZonesContext zone: zones) {
				
				zone.setObjectType(2);
	        	
	        	Long recordId = (Long) zone.getRecordId();
	        	Long markerModuleId = (Long) zone.getZoneModuleId();
	        	
	        	zoneRecordIds.add(zone.getSpace().getId());
	        	
	        	if (recordId != null && markerModuleId != null ) {
	        		
	        		List<Long> recordIds = zoneIdvsRecordIds.get(markerModuleId) == null ? new ArrayList<>() : zoneIdvsRecordIds.get(markerModuleId);
	     		
	        		if (recordIds != null) {
	        			recordIds.add(recordId);
	        			zoneIdvsRecordIds.put(markerModuleId, recordIds);
	        		}
	        		else {
	        			zoneIdvsRecordIds.put(markerModuleId, new ArrayList<>());
	            		recordIds.add(recordId);
	            		zoneIdvsRecordIds.put(markerModuleId, recordIds);
	        		}
	        	}
	        	
	        }
			
			
			
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);
            
            Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(spaceModule);

            
			  Collection<SupplementRecord>lookUpfields = new ArrayList<>();
        		List<FacilioField> fields = modBean.getAllFields(spaceModule.getName());
        		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
         		lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));
   			
			spaceList = V3RecordAPI.getRecordsListWithSupplements(spaceModule.getName(), zoneRecordIds, beanClassName, lookUpfields);
			
			spacesMap = spaceList.stream().collect( Collectors.toMap(r -> r.getId(), r -> r));
			
		}
		
		
        
        for (Long markerModuleId : markerIdvsRecordIds.keySet()) {
   	     List<Long> recordIds = markerIdvsRecordIds.get(markerModuleId);

        	if (CollectionUtils.isNotEmpty(recordIds)) {
                 ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                 FacilioModule module = modBean.getModule(markerModuleId);
                 
                 if(module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
                	 
                	  Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
                      
                      Collection<SupplementRecord>lookUpfields = new ArrayList<>();
              		List<FacilioField> fields = modBean.getAllFields(module.getName());
              		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
               		lookUpfields.add((LookupField) fieldMap.get("employee"));
               		lookUpfields.add((LookupField) fieldMap.get("department"));

           
           			markerIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, beanClassName, lookUpfields));
           	      Map<Long, ModuleBaseWithCustomFields> recordVsRecordMap = markerIdvsModuleListData.get(module.getName()).stream().collect(
   		                Collectors.toMap(r -> r.getId(), r -> r));
   		      
   		     markervsRecordObjectMap.put(markerModuleId, recordVsRecordMap);
                	 
                 }
                 else {

           			markerIdvsModuleListData.put(module.getName(),  V3RecordAPI.getRecordsList(module.getName(), recordIds));
           	      Map<Long, ModuleBaseWithCustomFields> recordVsRecordMap = markerIdvsModuleListData.get(module.getName()).stream().collect(
   		                Collectors.toMap(r -> r.getId(), r -> r));
   		      
   		     markervsRecordObjectMap.put(markerModuleId, recordVsRecordMap);
           			
                 }
             	
               
     		
     		        
        	}

        }
        
        
        
        // zone changes
        
        for (Long zoneModuleId : zoneIdvsRecordIds.keySet()) {
   	     List<Long> recordIds = zoneIdvsRecordIds.get(zoneModuleId);

        	if (CollectionUtils.isNotEmpty(recordIds)) {
                 ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                 FacilioModule module = modBean.getModule(zoneModuleId);
                 
                 if(module.getName().equals(FacilioConstants.ContextNames.LOCKERS)) {
                	 
                	Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
                      
                     Collection<SupplementRecord>lookUpfields = new ArrayList<>();
              		List<FacilioField> fields = modBean.getAllFields(module.getName());
              		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
               		lookUpfields.add((LookupField) fieldMap.get("employee"));
               		lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));

               		zoneIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, beanClassName, lookUpfields));
               		Map<Long, ModuleBaseWithCustomFields> recordVsRecordMap = zoneIdvsModuleListData.get(module.getName()).stream().collect(
   		                Collectors.toMap(r -> r.getId(), r -> r));
   		      
           	      	zonevsRecordObjectMap.put(zoneModuleId, recordVsRecordMap);
                	 
                 }
                 else {
                	 
                		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
                        
                        Collection<SupplementRecord>lookUpfields = new ArrayList<>();
                 		List<FacilioField> fields = modBean.getAllFields(module.getName());
                 		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                  		lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));

                  		zoneIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, beanClassName, lookUpfields));
                  		Map<Long, ModuleBaseWithCustomFields> recordVsRecordMap = zoneIdvsModuleListData.get(module.getName()).stream().collect(
      		                Collectors.toMap(r -> r.getId(), r -> r));
      		      
              	      	zonevsRecordObjectMap.put(zoneModuleId, recordVsRecordMap);
           			
                 }
    
        	}

        }
        
        

        
        for (V3MarkerContext marker: markers) {
        	Long recordId = (Long) marker.getRecordId();
        	Long markerModuleId = (Long) marker.getMarkerModuleId();
        	
        	V3IndoorFloorPlanGeoJsonContext feature = new V3IndoorFloorPlanGeoJsonContext();
            
        	feature.setObjectId(String.valueOf(marker.getId()));
        	String geometryStr = marker.getGeometry();
        	
        	if (geometryStr != null && !geometryStr.trim().isEmpty()) {
    			JSONObject geometry = (JSONObject) new JSONParser().parse(geometryStr);
            	feature.setGeometry(geometry);

    		}

        	feature.setType("Feature");
        	
        	
        	
        	if (recordId != null && markerModuleId != null ) {
     		
            	feature.setProperties(V3FloorPlanAPI.getMarkerProperties(markervsRecordObjectMap.get(markerModuleId).get(recordId), marker, markerModuleId, viewMode));
            	feature.setTooltipData(V3FloorPlanAPI.getMarkerTooltip(markervsRecordObjectMap.get(markerModuleId).get(recordId), marker, markerModuleId, viewMode));
        	}
        	else {
            	feature.setProperties(V3FloorPlanAPI.getMarkerProperties(null, marker,  null, viewMode));
            	feature.setTooltipData(V3FloorPlanAPI.getMarkerTooltip(null, marker, null, viewMode));
        	}
        	
        	feature.setActive(feature.getProperties().getActive());
        	feature.setObjectType(1);
        	
        	
        	
        	markerObjectMap.put(marker.getId(), feature);
        	
        	
        	
        }
        
        
        for(V3MarkerdZonesContext zone: zones) {
        	Long recordId = (Long) zone.getRecordId();
        	Long markerModuleId = (Long) zone.getZoneModuleId();
        	
        	zone.setSpace((SpaceContext)spacesMap.get(zone.getSpace().getId()));
        	
        	V3IndoorFloorPlanGeoJsonContext feature = new V3IndoorFloorPlanGeoJsonContext();
            
        	feature.setObjectId(String.valueOf(zone.getId()));
        	String geometryStr = zone.getGeometry();
        	
        	if (geometryStr != null && !geometryStr.trim().isEmpty()) {
    			JSONObject geometry = (JSONObject) new JSONParser().parse(geometryStr);
            	feature.setGeometry(geometry);

    		}

        	feature.setType("Feature");
        	
    
        	
        	if (recordId != null && markerModuleId != null ) {
        		feature.setProperties(V3FloorPlanAPI.getZoneProperties(zonevsRecordObjectMap.get(markerModuleId).get(recordId), zone, context, viewMode));
            	feature.setTooltipData(V3FloorPlanAPI.getZoneTooltipData(zone, viewMode));    	
           }
        	else {
        		feature.setProperties(V3FloorPlanAPI.getZoneProperties(zonevsRecordObjectMap.get(markerModuleId).get(recordId), zone, context, viewMode));
            	feature.setTooltipData(V3FloorPlanAPI.getZoneTooltipData(zone, viewMode));
        	}
        	feature.setObjectType(2);

        	zoneObjectMap.put(zone.getId(), feature);
        	
        	
   
        }

		// to get the floorplandata
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule floorplanModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
			Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(floorplanModule);
			Collection<SupplementRecord>floorplanLookUpfields = new ArrayList<>();
			List<FacilioField> floorplanFields = modBean.getAllFields(floorplanModule.getName());
			Map<String, FacilioField> floorplanFieldMap = FieldFactory.getAsMap(floorplanFields);
			floorplanLookUpfields.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.FLOOR));
			floorplanLookUpfields.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.BUILDING));

			List<Long> floorplanIds = new ArrayList<>();
			floorplanIds.add(floorplanId);
			
			List<V3IndoorFloorPlanContext> floorplanList = V3RecordAPI.getRecordsListWithSupplements(floorplanModule.getName(), floorplanIds, beanClassName, floorplanLookUpfields);
			
		
        V3IndoorFloorPlanContext floorplan = floorplanList.get(0);
       
        context.put(FacilioConstants.ContextNames.Floorplan.MARKERS, markerObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONES, zoneObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, floorplan);

        
		return false;
	}
	


}