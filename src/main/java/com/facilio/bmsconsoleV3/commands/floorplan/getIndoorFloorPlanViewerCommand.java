package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsoleV3.context.V3LockersContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.floorplan.*;
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
		JSONObject AssignmentSettings = new JSONObject();

		List<String> deskSecondaryLabelList = new ArrayList<>();
		List<String> spaceSecondaryLabelList = new ArrayList<>();

		Role role = AccountUtil.getCurrentUser().getRole();
		if (!role.isPrevileged()) {
			context.put("markerObject", (JSONObject)V3FloorPlanAPI.getMarkerObject());
		}


		// to get the floorplandata

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

//		FacilioModule floorplanModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
//		Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(floorplanModule);
//		Collection<SupplementRecord>floorplansupplements = new ArrayList<>();
//		List<FacilioField> floorplanFields = modBean.getAllFields(floorplanModule.getName());
//		Map<String, FacilioField> floorplanFieldMap = FieldFactory.getAsMap(floorplanFields);
//		floorplansupplements.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.FLOOR));
//		floorplansupplements.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.BUILDING));
//		floorplansupplements.add((LargeTextField)floorplanFieldMap.get(FacilioConstants.ContextNames.Floorplan.CUSTOMIZATION));
//		floorplansupplements.add((LargeTextField)floorplanFieldMap.get(FacilioConstants.ContextNames.Floorplan.CUSTOMIZATION_BOOKING));
//
//		List<Long> floorplanIds = new ArrayList<>();
//		floorplanIds.add(floorplanId);
//
//		List<V3IndoorFloorPlanContext> floorplanList = V3RecordAPI.getRecordsListWithSupplements(floorplanModule.getName(), floorplanIds, beanClassName, floorplansupplements);


		V3IndoorFloorPlanContext floorplan = (V3IndoorFloorPlanContext)context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
		String viewMode = (String) context.get(FacilioConstants.ContextNames.Floorplan.VIEW_MODE);
		
		if (viewMode == null) {
			viewMode = FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW;
		}

	    List<Long> objectIds = (List<Long>) context.get(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS);

		context.put("FLOORPLAN_ID", floorplanId);


		context.put("FLOORPLAN_MAPPED_MODULEOBJECT", V3FloorPlanAPI.getFloorplanMappedModules(context));

		context.put("FLOORPLAN_MAPPED_MODULES", V3FloorPlanAPI.getFloorplanMappedModuleNameFromModules(context));

		List<V3MarkerContext> markers = V3FloorPlanAPI.getAllMarkers(floorplanId, objectIds);
        
        List<V3MarkerdZonesContext> zones = V3FloorPlanAPI.getAllZones(floorplanId, objectIds);

        
        List<V3IndoorFloorPlanGeoJsonContext> features =  new ArrayList<V3IndoorFloorPlanGeoJsonContext>(); 
        
        Map<Long, V3IndoorFloorPlanGeoJsonContext> markerObjectMap = new HashMap<Long,V3IndoorFloorPlanGeoJsonContext >();
        Map<Long, V3IndoorFloorPlanGeoJsonContext> zoneObjectMap = new HashMap<Long,V3IndoorFloorPlanGeoJsonContext >();

		List<V3IndoorFloorPlanGeoJsonContext> geoMarkers = new ArrayList<>();
		List<V3IndoorFloorPlanGeoJsonContext> geoZones = new ArrayList<>();

        
        Map<Long, List<Long>> markerIdvsRecordIds = new HashMap<>();

        Map<String, List<V3ResourceContext>> markerIdvsModuleListData = new HashMap<>();
        
        Map<Long, List<Long>> zoneIdvsRecordIds = new HashMap<>();

        Map<String, List<V3ResourceContext>> zoneIdvsModuleListData = new HashMap<>();
        
        Map<Long, SpaceContext> spacesMap = new HashMap<>();
        
        List<SpaceContext> spaceList = new ArrayList<SpaceContext>(); 

        
        
        Map<Long,  Map<Long, V3ResourceContext>> markervsRecordObjectMap= new HashMap<>();

        Map<Long, Map<Long, V3ResourceContext>> zonevsRecordObjectMap= new HashMap<>();

		
        
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
			
			
			
            FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);
            
            Class spacebeanClassName = FacilioConstants.ContextNames.getClassFromModule(spaceModule);

            
			  Collection<SupplementRecord>lookUpfields = new ArrayList<>();
        		List<FacilioField> fields = modBean.getAllFields(spaceModule.getName());
        		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
         		lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));

   			
			spaceList = V3RecordAPI.getRecordsListWithSupplements(spaceModule.getName(), zoneRecordIds, spacebeanClassName, lookUpfields);
			
			spacesMap = spaceList.stream().collect( Collectors.toMap(r -> r.getId(), r -> r));
			
		}
		
		
        
        for (Long markerModuleId : markerIdvsRecordIds.keySet()) {
   	     List<Long> recordIds = markerIdvsRecordIds.get(markerModuleId);

        	if (CollectionUtils.isNotEmpty(recordIds)) {
                 FacilioModule module = modBean.getModule(markerModuleId);
                 
                 if(module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
                	 
                	  Class deskbeanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
                      
                      Collection<SupplementRecord>lookUpfields = new ArrayList<>();
              		List<FacilioField> fields = modBean.getAllFields(module.getName());
              		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
               		lookUpfields.add((LookupField) fieldMap.get("employee"));
               		lookUpfields.add((LookupField) fieldMap.get("department"));

           
           			markerIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, deskbeanClassName, lookUpfields));
           	      Map<Long, V3ResourceContext> recordVsRecordMap = markerIdvsModuleListData.get(module.getName()).stream().collect(
   		                Collectors.toMap(r -> r.getId(), r -> r));
   		      
   		     markervsRecordObjectMap.put(markerModuleId, recordVsRecordMap);
                	 
                 }
                 else {
					// Class moduleBeanClass = FacilioConstants.ContextNames.getClassFromModule(module);

           			markerIdvsModuleListData.put(module.getName(),  V3RecordAPI.getRecordsList(module.getName(), recordIds, V3ResourceContext.class));
           	      Map<Long, V3ResourceContext> recordVsRecordMap = markerIdvsModuleListData.get(module.getName()).stream().collect(
   		                Collectors.toMap(r -> r.getId(), r -> r));
   		      
   		     markervsRecordObjectMap.put(markerModuleId, recordVsRecordMap);
           			
                 }
             	
               
     		
     		        
        	}

        }


//		// get the floorplan settings
//		if (floorplan != null) {
//			V3FloorplanCustomizationContext floorplanAssign = floorplan.getCustomization();
//			V3FloorplanCustomizationContext floorplanBooking = floorplan.getCustomizationBooking();
//            context.put("FLOORPLAN_ASSIGNMENT_CUSTOMIZATION", floorplanAssign);
//            context.put("FLOORPLAN_BOOKING_CUSTOMIZATION", floorplanBooking);
//		}


		// zone changes


		for (Long zoneModuleId : zoneIdvsRecordIds.keySet()) {
   	     List<Long> recordIds = zoneIdvsRecordIds.get(zoneModuleId);

        	if (CollectionUtils.isNotEmpty(recordIds)) {
                 FacilioModule module = modBean.getModule(zoneModuleId);
                 
                 if(module.getName().equals(FacilioConstants.ContextNames.LOCKERS)) {
                	 
                	Class lockerbeanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
                      
                     Collection<SupplementRecord>lookUpfields = new ArrayList<>();
              		List<FacilioField> fields = modBean.getAllFields(module.getName());
              		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
               		lookUpfields.add((LookupField) fieldMap.get("employee"));
               		lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));

               		zoneIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, lockerbeanClassName, lookUpfields));
               		Map<Long, V3ResourceContext> recordVsRecordMap = zoneIdvsModuleListData.get(module.getName()).stream().collect(
   		                Collectors.toMap(r -> r.getId(), r -> r));
   		      
           	      	zonevsRecordObjectMap.put(zoneModuleId, recordVsRecordMap);
                	 
                 }
				 else if(module.getName().equals(FacilioConstants.ContextNames.PARKING_STALL)) {

					 Class parkingBeanclass = FacilioConstants.ContextNames.getClassFromModule(module);

					 Collection<SupplementRecord>lookUpfields = new ArrayList<>();
					 List<FacilioField> fields = modBean.getAllFields(module.getName());
					 Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
					 lookUpfields.add((LookupField) fieldMap.get("employee"));
					 lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));

					 zoneIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, parkingBeanclass, lookUpfields));
					 Map<Long, V3ResourceContext> recordVsRecordMap = zoneIdvsModuleListData.get(module.getName()).stream().collect(
							 Collectors.toMap(r -> r.getId(), r -> r));

					 zonevsRecordObjectMap.put(zoneModuleId, recordVsRecordMap);

				 }
                 else {
                	 
                		Class spaceCatbeanClassName = FacilioConstants.ContextNames.getClassFromModule(module);

                        Collection<SupplementRecord>lookUpfields = new ArrayList<>();
                 		List<FacilioField> fields = modBean.getAllFields(module.getName());
                 		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                  		lookUpfields.add((LookupField) fieldMap.get(FacilioConstants.ContextNames.SPACE_CATEGORY_FIELD));

                  		zoneIdvsModuleListData.put(module.getName(), V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, V3ResourceContext.class, lookUpfields));
                  		Map<Long, V3ResourceContext> recordVsRecordMap = zoneIdvsModuleListData.get(module.getName()).stream().collect(
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
     		
            	feature.setProperties(V3FloorPlanAPI.getMarkerProperties(markervsRecordObjectMap.get(markerModuleId).get(recordId), marker, markerModuleId, viewMode, context));
            	feature.setTooltipData(V3FloorPlanAPI.getMarkerTooltip(markervsRecordObjectMap.get(markerModuleId).get(recordId), marker, markerModuleId, viewMode, context));
        	}
        	else {
            	feature.setProperties(V3FloorPlanAPI.getMarkerProperties(null, marker,  null, viewMode, context));
            	feature.setTooltipData(V3FloorPlanAPI.getMarkerTooltip(null, marker, null, viewMode, context));
        	}
        	
        	feature.setActive(feature.getProperties().getActive());
        	feature.setObjectType(1);
        	feature.setMarkerType(marker.getMarkerType());

        	
        	
        	markerObjectMap.put(marker.getId(), feature);

			geoMarkers.add(feature);

        	
        }
        
        
        for(V3MarkerdZonesContext zone: zones) {
        	Long recordId = (Long) zone.getRecordId();
        	Long markerModuleId = (Long) zone.getZoneModuleId();
        	
        	zone.setSpace((SpaceContext)spacesMap.get(zone.getSpace().getId()));

			V3IndoorFloorPlanGeoJsonContext feature = new V3IndoorFloorPlanGeoJsonContext();
            
        	feature.setObjectId(String.valueOf(zone.getId()));
			feature.setIsReservable(zone.isIsReservable());

			String geometryStr = zone.getGeometry();
        	
        	if (geometryStr != null && !geometryStr.trim().isEmpty()) {
    			JSONObject geometry = (JSONObject) new JSONParser().parse(geometryStr);
            	feature.setGeometry(geometry);

    		}

        	feature.setType("Feature");
        	
    
        	
        	if (recordId != null && markerModuleId != null ) {
        		feature.setProperties(V3FloorPlanAPI.getZoneProperties(zonevsRecordObjectMap.get(markerModuleId).get(recordId), zone, context, viewMode, markerModuleId));
            	feature.setTooltipData(V3FloorPlanAPI.getZoneTooltipData(zone, viewMode,context));
           }
        	else {
        		feature.setProperties(V3FloorPlanAPI.getZoneProperties(null, zone, context, viewMode, null));
            	feature.setTooltipData(V3FloorPlanAPI.getZoneTooltipData(zone, viewMode,context));
        	}
        	feature.setObjectType(2);

        	zoneObjectMap.put(zone.getId(), feature);
        	geoZones.add(feature);
   
        }


       
        context.put(FacilioConstants.ContextNames.Floorplan.MARKERS, markerObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONES, zoneObjectMap);
		context.put("GEO_MARKERS", geoMarkers);
		context.put("GEO_ZONES", geoZones);

		return false;
	}
	


}