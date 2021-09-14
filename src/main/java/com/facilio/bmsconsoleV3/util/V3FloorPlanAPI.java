package com.facilio.bmsconsoleV3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3LockersContext;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanPropertiesContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

public class V3FloorPlanAPI {


    	@SuppressWarnings("unchecked")
		public static JSONObject getZoneTooltipData(V3MarkerdZonesContext zone, String viewMode) throws Exception {


		 JSONObject tooltip = new JSONObject();
		 JSONObject tooltipCoreData = new JSONObject();
		 List <JSONObject> tooltipConent = new ArrayList<>(); 
		 
		 
		 
		 tooltipCoreData = new JSONObject();
		 tooltipCoreData.put("icon", "room");
		 if (zone.getSpace() != null) {
			 tooltipCoreData.put("label",  zone.getSpace().getName());
			 
		 }
		 tooltip.put("title", tooltipCoreData);
		 
	
		 
		 tooltipCoreData = new JSONObject();
		 tooltipCoreData.put("icon", "desk_type");
		 if (zone.getSpace() != null && zone.getSpace().getSpaceCategory() != null && zone.getSpace().getSpaceCategory().getName() != null) {
			 tooltipCoreData.put("label", zone.getSpace().getSpaceCategory().getName());
			 
		 }
		 tooltipConent.add(tooltipCoreData);
		 
		 tooltip.put("content", tooltipConent);
		 
		 
		 tooltipCoreData = new JSONObject();
		 tooltipCoreData.put("icon", "calender");
		 if (zone.getSpace() != null) {
			String label = zone.isIsReservable() ? "Reservable" : "Non Reservable";
			 tooltipCoreData.put("label", label);
			 
		 }
		 tooltipConent.add(tooltipCoreData);
		
         return tooltip;
     }
	
	 @SuppressWarnings({ "unchecked", "unchecked" })
	public static JSONObject getMarkerTooltip(ModuleBaseWithCustomFields record, V3MarkerContext marker,Long markerModuleId, String viewMode) throws Exception {


		 JSONObject tooltip = new JSONObject();
		 JSONObject tooltipCoreData = new JSONObject();
		 List <JSONObject> tooltipConent = new ArrayList<>(); 
		 
		 
		   if (record != null) {
				  ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		          FacilioModule module = modBean.getModule(markerModuleId);
			   
			   if(module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
				   
				   V3DeskContext desk = (V3DeskContext) record;
				   
				   
				   	// title record
					 tooltipCoreData.put("icon", "office_desk");
					 tooltipCoreData.put("label", desk.getName());
					 tooltip.put("title", tooltipCoreData);
					 
					 tooltipCoreData = new JSONObject();
					 tooltipCoreData.put("icon", "desk_type");
					 tooltipCoreData.put("label", desk.getDeskTypeVal());
					 tooltipConent.add(tooltipCoreData);

					 
					 if (desk.getEmployee() != null) {
						 
						 tooltipCoreData = new JSONObject();
						 tooltipCoreData.put("icon", "employee");
						 tooltipCoreData.put("label", desk.getEmployee().getName());
						 tooltipConent.add(tooltipCoreData);

					 }
					 else {
						 if (desk.getDeskType() == 1) {
							 tooltipCoreData = new JSONObject();
							 tooltipCoreData.put("icon", "employee");
							 tooltipCoreData.put("label", "Unassigned");
							 tooltipConent.add(tooltipCoreData);
						 }
						 
						 
					 }
					 
					 if (desk.getDepartment() != null) {
						 tooltipCoreData = new JSONObject();
						 tooltipCoreData.put("icon", "department");
						 tooltipCoreData.put("label",  desk.getDepartment().getName());
						 tooltipConent.add(tooltipCoreData);

					 }
					 
					 tooltip.put("content", tooltipConent);

			   }

		   }
		   else {
			    
				 tooltipCoreData = new JSONObject();
				 tooltipCoreData.put("icon", "desk_type");
				 tooltipCoreData.put("label",  marker.getLabel());
				 tooltipConent.add(tooltipCoreData);
				 
				 tooltip.put("content", tooltipConent);

		   }
		
         return tooltip;
     }

	 	   
	 	  public static V3IndoorFloorPlanPropertiesContext getZoneProperties(ModuleBaseWithCustomFields record, V3MarkerdZonesContext zone, Context context, String viewMode) throws Exception {
	 		    
	 			
	 	        V3IndoorFloorPlanPropertiesContext properties = new V3IndoorFloorPlanPropertiesContext();
	 	         
	 	    	Long markerModuleId = (long) zone.getZoneModuleId();
	 		   
	 			   properties.setObjectId(zone.getId());
	 			   properties.setRecordId(zone.getRecordId());
	 			   properties.setMarkerModuleId(zone.getZoneModuleId());
	 			if (zone.getSpace() != null) {
	 				properties.setSpaceId(zone.getSpace().getId());
	 				
	 			    properties.setLabel(zone.getSpace().getName());

	 				if (zone.getSpace().getSpaceCategory() != null && zone.getSpace().getSpaceCategory().getName() != null) {
	 					properties.setSpaceCategory(zone.getSpace().getSpaceCategory().getName());

	 				}
	 				
	 				properties.setZoneBackgroundColor("000000");
	 				properties.setIsOccupied(false);
	 				
	 			
	 				if (markerModuleId != null) {
	 					
	 					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	 			        FacilioModule module = modBean.getModule(markerModuleId);
	 			        
	 			        properties.setMarkerModuleName(module.getName());
	 					
	 					if (module.getName().equals(FacilioConstants.ContextNames.LOCKERS)) {
	 						
	 						V3LockersContext locker = (V3LockersContext) record;
	 						
	 						if (locker.getEmployee() != null) {
	 							properties.setIsOccupied(true);
	 							properties.setEmployeeId(locker.getEmployee().getId());
	 							properties.setZoneBackgroundColor("#dc4a4c");
	 						}
	 						else {
	 							properties.setZoneBackgroundColor("#22ae5c");
	 						}
	 						
	 						
	 						
	 					}
	 					else if (module.getName().equals(FacilioConstants.ContextNames.PARKING_STALL)) {
	 						V3ParkingStallContext parking = (V3ParkingStallContext) record;
	 						

	 						
	 						if (parking.getModuleState() != null) {
	 				        	FacilioStatus status = TicketAPI.getStatus(module, "Occupied");
	 				        	FacilioStatus vacantStatus = TicketAPI.getStatus(module, "Vacant");

	 				        	
	 							if (parking.getModuleState().getId() == status.getId()) {
	 								properties.setIsOccupied(true);
	 								properties.setZoneBackgroundColor("#dc4a4c");
	 							}
	 							else if (parking.getModuleState().getId() == vacantStatus.getId()) {
	 								properties.setZoneBackgroundColor("#22ae5c");
	 							}
	 						}
	 						
	 					}
	 					
	 				    if (viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW)) {
	 				    	if (zone.isIsReservable()) {
		 						properties.setZoneBackgroundColor("#0D5BE1");
		 					}
	 				   }
	 					
	 					
	 					
	 				}			
	 			
	 				
	 			}
	 			else {
	 			    properties.setLabel(zone.getLabel());

	 			}

	 			return properties;
	 		   
	 	   }
	 
	
	   public static V3IndoorFloorPlanPropertiesContext getMarkerProperties(ModuleBaseWithCustomFields record, V3MarkerContext marker,Long markerModuleId, String viewMode) throws Exception {
	    
	          V3IndoorFloorPlanPropertiesContext properties = new V3IndoorFloorPlanPropertiesContext();
		   
		   if (record != null && markerModuleId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	          FacilioModule module = modBean.getModule(markerModuleId);
			   V3ResourceContext recordData = (V3ResourceContext) record;
			   properties.setLabel(recordData.getName());
			   properties.setObjectId(marker.getId());
			   properties.setRecordId(marker.getRecordId());
			   properties.setMarkerModuleId(marker.getMarkerModuleId());
			   properties.setMarkerModuleName(module.getName());
			   
			   properties.setActive(true);
			   
			   if (marker.getMarkerType() != null) {
				   properties.setMarkerId(String.valueOf(marker.getMarkerType().getId()));
			   }
			   
			   if(module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
				   // to handle desk details here
				   
				   V3DeskContext desk = (V3DeskContext) record;
				   String iconName = "desk";
				   properties.setDeskCode(desk.getDeskCode());
				   properties.setDeskId(desk.getId());
				   properties.setDeskType(desk.getDeskType());
				   
				   properties.setIsCustom(true); // need to change based on the desktype
				   

				   if (viewMode.equals(FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW)) {
					if (desk.getDeskType() == 1) {
					   properties.setActive(true);
				   }
				   else {
					   properties.setActive(false);

				   }

				   if (desk.getDepartment() != null) {
					   properties.setDepartmentId(desk.getDepartment().getId());
				   }
				   
				   if (desk.getEmployee() != null) {
					   properties.setEmployeeId(desk.getEmployee().getId());
					   properties.setSecondaryLabel(desk.getEmployee().getName());
					   properties.setCenterLabel(getCenterLabel(desk.getEmployee().getName()));
					   properties.setIconName(iconName);
					
						iconName = iconName.concat("_" + desk.getDeskTypeVal());


					   if (desk.getDepartment() != null) {
						   iconName = iconName.concat("_" + desk.getDepartment().getName());
					   }
					   
				   }
				   
				   }
				   else if (viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW)) {
					if (desk.getDeskType() == 3) {
					   properties.setActive(true);
				   }
				   else {
					   properties.setActive(false);

				   }

				   if (desk.getDepartment() != null) {
					   properties.setDepartmentId(desk.getDepartment().getId());
				   }
				   
				   if (desk.getEmployee() != null) {
					   properties.setEmployeeId(desk.getEmployee().getId());
					   properties.setSecondaryLabel(desk.getEmployee().getName());
					   properties.setIconName(iconName);

					   // set booking desk icon name

				   }
				   
				   }
				   else {
					      if (desk.getDepartment() != null) {
					   properties.setDepartmentId(desk.getDepartment().getId());
				   }
				   
				   if (desk.getEmployee() != null) {
					   properties.setEmployeeId(desk.getEmployee().getId());
					   properties.setSecondaryLabel(desk.getEmployee().getName());
					   properties.setIconName(iconName);
					
						iconName = iconName.concat("_" + desk.getDeskTypeVal());


					   if (desk.getDepartment() != null) {
						   iconName = iconName.concat("_" + desk.getDepartment().getName());
					   }
					   
				   }
				   }
				   
				   
				   // set icon state class
				   
				   iconName = iconName.replaceAll("\\s", ""); //  itw ill remove the space names
				   properties.setActiveClass(iconName.concat("_active"));
				   properties.setNormalClass(iconName);
				   properties.setHoverClass(iconName.concat("_hover"));
			      
				   //

				   properties.setMarkerId(iconName);

			   }
			   else {
				   properties.setIsCustom(false);

			   }

			   
		   }
		   else {
			   properties.setActive(true);
			   properties.setLabel(marker.getLabel());
			   	properties.setObjectId(marker.getId());
			   properties.setRecordId(marker.getRecordId());
			   properties.setMarkerModuleId(null);
			   properties.setMarkerModuleName(null);
			   properties.setIsCustom(false);

  			if (marker.getMarkerType() != null) {
				  long markerId = marker.getMarkerType().getId();
				   properties.setMarkerId(String.valueOf(markerId));
				   properties.setActiveClass(String.valueOf(markerId));
				   properties.setNormalClass(String.valueOf(markerId));
				   properties.setHoverClass(String.valueOf(markerId));

			   }
				 

		   }
		return properties;
		   
	    }
	   
	   public static String getCenterLabel(String name) {
		   String result = "";
		   
		   if (name != null) {
			   String[] splitStr = name.split("\\s+");
			   
			   if (splitStr.length > 0) {
				   
				   if (splitStr.length == 1) {
					   if (splitStr[0].length() > 1) {
						   result = splitStr[0].charAt(0) + "" + splitStr[0].charAt(1); 
					   }
					   else {
						   result = splitStr[0].charAt(0) + ""; 
					   }
					   
				   }
				   else {
					    result = splitStr[0].charAt(0) + "";
						 if (splitStr[1] != null) {
					   		result = result + splitStr[1].charAt(0); 
				   			}
				   }
	 
			   }
			   
			   return result.toUpperCase();
		   }
		   return name;

	   }

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
         return getAllZones(parentId, null);
      }

    public static List<V3MarkerdZonesContext> getAllZones(Long parentId, List<Long> objectIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

        SelectRecordsBuilder<V3MarkerdZonesContext> builder = new SelectRecordsBuilder<V3MarkerdZonesContext>()
                .module(module)
                .beanClass(V3MarkerdZonesContext.class)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getCondition("FLOORPLAN_ID", "floorplanId",
						String.valueOf(parentId), NumberOperators.EQUALS));

        
        if (objectIds != null && objectIds.size() > 0) {
        	builder.andCondition(CriteriaAPI.getIdCondition(objectIds, module));
        }
        
        builder.fetchSupplement((LookupField) fieldMap.get("space"));

        List<V3MarkerdZonesContext> zones = builder.get();
        if(CollectionUtils.isNotEmpty(zones))
        {
            return zones;
        }
        return new ArrayList<>();
    }
    public static List<V3MarkerContext> getAllMarkers(Long parentId) throws Exception {
       return getAllMarkers(parentId, null);
    }
    public static List<V3MarkerContext> getAllMarkers(Long parentId, List<Long> objectIds) throws Exception {
 	   ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
       FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
       Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
       SelectRecordsBuilder<V3MarkerContext> builder = new SelectRecordsBuilder<V3MarkerContext>()
	                .module(module)
	                .beanClass(V3MarkerContext.class)
	                .select(modBean.getAllFields(module.getName()))
	                .andCondition(CriteriaAPI.getCondition("FLOORPLAN_ID", "floorplanId",
							String.valueOf(parentId), NumberOperators.EQUALS));
	        
	     if (objectIds != null && objectIds.size() > 0) {
	    	builder.andCondition(CriteriaAPI.getIdCondition(objectIds, module));

	     }
	        
	        List<V3MarkerContext> markers = builder.get();
	        if(CollectionUtils.isNotEmpty(markers))
	        {
	            return markers;
	        }
    	return new ArrayList<>();
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
