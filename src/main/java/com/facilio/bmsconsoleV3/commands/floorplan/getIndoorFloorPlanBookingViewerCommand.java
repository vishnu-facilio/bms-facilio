package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
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
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;

import java.util.*;
import java.util.stream.Collectors;

public class getIndoorFloorPlanBookingViewerCommand extends FacilioCommand {

	@SuppressWarnings({ "null", "unchecked" })
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Criteria criteria = null;


	    long floorplanId = (long) context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID);
		String viewMode = (String) context.get(FacilioConstants.ContextNames.Floorplan.VIEW_MODE);
		if (viewMode == null) {
			viewMode = FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW;
		}
	    
	    List<Long> objectIds = (List<Long>) context.get(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS);
	    
	    Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
	    Long endTime = (Long) context.get(FacilioConstants.ContextNames.ENDTIME);
		
        List<V3MarkerContext> markers = V3FloorPlanAPI.getAllMarkers(floorplanId, objectIds);
        
        List<V3MarkerdZonesContext> zones = V3FloorPlanAPI.getAllZones(floorplanId, objectIds);

        
        List<V3IndoorFloorPlanGeoJsonContext> features =  new ArrayList<V3IndoorFloorPlanGeoJsonContext>(); 
        
        Map<Long, List<Long>> markerIdvsRecordIds = new HashMap<>();

        Map<String, List<ModuleBaseWithCustomFields>> markerIdvsModuleListData = new HashMap<>();
        
        Map<Long, List<Long>> zoneIdvsRecordIds = new HashMap<>();

        Map<String, List<ModuleBaseWithCustomFields>> zoneIdvsModuleListData = new HashMap<>();
        
        Map<Long, SpaceContext> spacesMap = new HashMap<>();
        
        List<SpaceContext> spaceList = new ArrayList<SpaceContext>(); 
        
        Map<Long,  Map<Long, ModuleBaseWithCustomFields>> markervsRecordObjectMap= new HashMap<>();

        Map<Long,  Map<Long, ModuleBaseWithCustomFields>> zonevsRecordObjectMap= new HashMap<>();
        
        List<Long> bookingSpaceIds = new ArrayList<Long>(); 
        

        
        
        
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
	        	
	        	bookingSpaceIds.add(zone.getSpace().getId());
	        	
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

				Condition condition = new Condition();
				condition.setColumnName("DESK_TYPE");
				condition.setFieldName("deskType");
				condition.setOperator(NumberOperators.EQUALS);
				condition.setValue("3");

				criteria = new Criteria();
				criteria.addAndCondition(condition);
				
				List<V3DeskContext> deskList =  V3RecordAPI.getRecordsListWithSupplements(module.getName(), recordIds, beanClassName, criteria, lookUpfields);
				if (CollectionUtils.isNotEmpty(deskList)) {
					bookingSpaceIds.addAll(deskList.stream().map(r -> r.getId()).collect(Collectors.toList()));
				}
				
	 
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
				else if(module.getName().equals(FacilioConstants.ContextNames.PARKING_STALL)) {

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
        
        // to get the floorplan data

        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule floorplanModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
			Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(floorplanModule);
			Collection<SupplementRecord>floorplansupplements = new ArrayList<>();
			List<FacilioField> floorplanFields = modBean.getAllFields(floorplanModule.getName());
			Map<String, FacilioField> floorplanFieldMap = FieldFactory.getAsMap(floorplanFields);
			floorplansupplements.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.FLOOR));
			floorplansupplements.add((LookupField) floorplanFieldMap.get(FacilioConstants.ContextNames.BUILDING));
			floorplansupplements.add((LargeTextField)floorplanFieldMap.get(FacilioConstants.ContextNames.Floorplan.CUSTOMIZATION));
		    floorplansupplements.add((LargeTextField)floorplanFieldMap.get(FacilioConstants.ContextNames.Floorplan.CUSTOMIZATION_BOOKING));

			List<Long> floorplanIds = new ArrayList<>();
			floorplanIds.add(floorplanId);
			
			List<V3IndoorFloorPlanContext> floorplanList = V3RecordAPI.getRecordsListWithSupplements(floorplanModule.getName(), floorplanIds, beanClassName, floorplansupplements);
			
		
        V3IndoorFloorPlanContext floorplan = floorplanList.get(0);

		if (floorplan != null) {
			V3FloorplanCustomizationContext floorplanAssign = floorplan.getCustomization();
			V3FloorplanCustomizationContext floorplanBooking = floorplan.getCustomizationBooking();
			context.put("FLOORPLAN_ASSIGNMENT_CUSTOMIZATION", floorplanAssign);
			context.put("FLOORPLAN_BOOKING_CUSTOMIZATION", floorplanBooking);
		}

        
        context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, floorplan);

        
        context.put(FacilioConstants.ContextNames.SPACE_LIST, bookingSpaceIds);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.Floorplan.MARKER_RECORD_OBJECTMAP, markervsRecordObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONE_RECORD_OBJECTMAP, zonevsRecordObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.MARKER_LIST, markers);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONE_LIST, zones);
        context.put(FacilioConstants.ContextNames.Floorplan.SPACE_MAP, spacesMap);
        

		return false;
	}
}