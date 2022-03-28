package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3LockersContext;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;


import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.lang3.StringUtils;


import java.util.*;
import java.util.stream.Collectors;

public class getIndoorFloorPlanBookingResultCommands extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Criteria criteria = null;

        
        Map<Long,  Map<Long, ModuleBaseWithCustomFields>> markervsRecordObjectMap = (Map<Long, Map<Long, ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.Floorplan.MARKER_RECORD_OBJECTMAP);

        Map<Long,  Map<Long, ModuleBaseWithCustomFields>> zonevsRecordObjectMap= (Map<Long, Map<Long, ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.Floorplan.ZONE_RECORD_OBJECTMAP);
        
      
        List<V3MarkerContext> markers = (List<V3MarkerContext>) context.get(FacilioConstants.ContextNames.Floorplan.MARKER_LIST);
        
        List<V3MarkerdZonesContext> zones = (List<V3MarkerdZonesContext>) context.get(FacilioConstants.ContextNames.Floorplan.ZONE_LIST);
    
        
        Map<Long, V3IndoorFloorPlanGeoJsonContext> markerObjectMap = new HashMap<Long,V3IndoorFloorPlanGeoJsonContext >();
        Map<Long, V3IndoorFloorPlanGeoJsonContext> zoneObjectMap = new HashMap<Long,V3IndoorFloorPlanGeoJsonContext >();

        Map<Long, SpaceContext> spacesMap =  (Map<Long, SpaceContext>) context.get(FacilioConstants.ContextNames.Floorplan.SPACE_MAP);

    	String viewMode = (String) context.get(FacilioConstants.ContextNames.Floorplan.VIEW_MODE);
    	
      
    	
		if (viewMode == null) {
			viewMode = FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW;
		}
		
		
		if (CollectionUtils.isNotEmpty(markers)) {
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

		            	feature.setProperties(getMarkerProperties(markervsRecordObjectMap.get(markerModuleId).get(recordId), marker, markerModuleId,  context));
		            	feature.setTooltipData(getMarkerTooltip(markervsRecordObjectMap.get(markerModuleId).get(recordId), marker, markerModuleId , context, feature.getProperties()));
		        	}
		        	else {
		            	feature.setProperties(getMarkerProperties(null, marker,  null, context));
		            	feature.setTooltipData(getMarkerTooltip(null, marker, null, context, feature.getProperties()));
		        	}
		        	
		        	feature.setActive(feature.getProperties().getActive());
		        	feature.setObjectType(1);
		        	feature.setMarkerType(marker.getMarkerType());
		        	
		        	
		        	
		        	markerObjectMap.put(marker.getId(), feature);
		        	
		        	
		        	
		        }
		}
      
        
		if (CollectionUtils.isNotEmpty(zones)) {
			 for(V3MarkerdZonesContext zone: zones) {
		        	Long recordId = (Long) zone.getRecordId();
		        	Long markerModuleId = (Long) zone.getZoneModuleId();
		        	
		        	zone.setSpace((SpaceContext)spacesMap.get(zone.getSpace().getId()));
		        	
		        	
		        	V3IndoorFloorPlanGeoJsonContext feature = new V3IndoorFloorPlanGeoJsonContext();
		        	
		        	feature.setIsReservable(zone.isIsReservable());
		            
		        	feature.setObjectId(String.valueOf(zone.getId()));
		        	String geometryStr = zone.getGeometry();
		        	
		        	if (geometryStr != null && !geometryStr.trim().isEmpty()) {
		    			JSONObject geometry = (JSONObject) new JSONParser().parse(geometryStr);
		            	feature.setGeometry(geometry);

		    		}

		        	feature.setType("Feature");
		        	
		    
		        	
		        	if (recordId != null && markerModuleId != null ) {
		        		feature.setProperties(V3FloorPlanAPI.getZoneProperties(zonevsRecordObjectMap.get(markerModuleId).get(recordId), zone, context, viewMode, markerModuleId));
		            	feature.setTooltipData(getZoneTooltipData(zone, context));    	
		           }
		        	else {
		        		feature.setProperties(V3FloorPlanAPI.getZoneProperties(null, zone, context, viewMode, null));
		            	feature.setTooltipData(getZoneTooltipData(zone, context));
		        	}
		        	feature.setObjectType(2);

		        	zoneObjectMap.put(zone.getId(), feature);
		        	
		        	
		   
		        }
		          
		}
        
       
       
        context.put(FacilioConstants.ContextNames.Floorplan.MARKERS, markerObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONES, zoneObjectMap);

        
		return false;
	}
	
	public static V3IndoorFloorPlanPropertiesContext getMarkerProperties(ModuleBaseWithCustomFields record, V3MarkerContext marker,Long markerModuleId, Context context) throws Exception {
	    
	        Map<Long, List<BookingSlotsContext>> facilityBookingsMap = (Map<Long, List<BookingSlotsContext>>) context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);

			Map<Long, V3FacilityBookingContext> bookingMap =  (Map<Long, V3FacilityBookingContext>) context.get("bookingMap");

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
			   properties.setMarkerId(String.valueOf(marker.getMarkerType().getName()));
		   }
		   
		   if(module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
			   
			   // to handle desk details here
			   
			   V3DeskContext desk = (V3DeskContext) record;
			   properties.setDeskType(desk.getDeskType());
			   
			   String iconName = "desk";
			  
				if (desk.getDeskType() == 3) {
					iconName = iconName.concat("_hot");
				   properties.setActive(true);
			   }
				else if (desk.getDeskType() == 2) {
					iconName = iconName.concat("_hotel");
					 properties.setActive(false);
				}
			   else {
				   properties.setActive(false);

			   }
			   
			   
				if (desk.getDeskType() != 1) {
					if (!CollectionUtils.sizeIsEmpty(facilityBookingsMap)) {
						   List<BookingSlotsContext> facilityBooking = (List<BookingSlotsContext>) facilityBookingsMap.get(desk.getId());

					 if (facilityBooking != null && facilityBooking.size() > 0) {
						   properties.setSecondaryLabel(getSecondaryLabelFromBookingsSlots(facilityBooking, bookingMap));
						   properties.setCenterLabel("");
						   iconName = iconName.concat("_booked");
						   properties.setIsBooked(true);
					   }
					 else {
						   properties.setCenterLabel("");
						   iconName = iconName.concat("_available");
						   properties.setIsBooked(false);
					   }
					  
					}
					 else {
						   properties.setCenterLabel("");
						   iconName = iconName.concat("_available");
						   properties.setIsBooked(false);
					   }
					
				}
	
			   properties.setDeskCode(desk.getDeskCode());
			   properties.setDeskId(desk.getId());
			   
			   properties.setIsCustom(true); // need to change based on the desktype


			   V3FloorplanCustomizationContext bookingCustomization = (V3FloorplanCustomizationContext) context.get("FLOORPLAN_BOOKING_CUSTOMIZATION");
			   properties.setLabel(bookingCustomization.getDeskSecondaryLabel().getLabelType().format(desk));
			   properties.setSecondaryLabel(bookingCustomization.getDeskPrimaryLabel().getLabelType().format(desk));


			   if (desk.getDepartment() != null) {
				   properties.setDepartmentId(desk.getDepartment().getId());
			   }

			   if (desk.getEmployee() != null) {
				   properties.setEmployeeId(desk.getEmployee().getId());
				   if (desk.getDeskType() == 1) {
					   properties.setCenterLabel(V3FloorPlanAPI.getCenterLabel(desk.getEmployee().getName()));
					   iconName = "desk";
					   properties.setIconName(iconName);
					
						iconName = iconName.concat("_" + desk.getDeskTypeName());


					   if (desk.getDepartment() != null) {
						   iconName = iconName.concat("_" + desk.getDepartment().getName());
					   }
				   }
				  
				   // set booking desk icon name

			   }
			   
			   // set icon state class
			   
			   iconName = iconName.replaceAll("\\s", ""); //  itw ill remove the space names
			   properties.setActiveClass(iconName.concat("_active"));
			   properties.setNormalClass(iconName);
			   properties.setHoverClass(iconName.concat("_hover"));
			   properties.setIconName(iconName);
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
			  String markerId = marker.getMarkerType().getName();
			   properties.setMarkerId(String.valueOf(markerId));
			   properties.setActiveClass(String.valueOf(markerId));
			   properties.setNormalClass(String.valueOf(markerId));
			   properties.setHoverClass(String.valueOf(markerId));

		   }
			 

	   }
	return properties;
	   
  }
	
	private static String getSecondaryLabelFromBookingsSlots( List<BookingSlotsContext>  bookingSlots, Map<Long, V3FacilityBookingContext> bookingMap) {
		
		Map<Long, String> bookingRequester = new HashMap<>();
		

		bookingSlots.forEach(r -> {
			if(r.getBooking() != null) {
				V3FacilityBookingContext booking = bookingMap.get(r.getBooking().getId());
				if (booking.getReservedFor() != null && booking.getReservedFor().getName() != null) {
					bookingRequester.put(booking.getReservedFor().getId(), booking.getReservedFor().getName());
				}
			}
		});
				
		return String.join(", ",bookingRequester.values());
	}
	

	
 @SuppressWarnings("unchecked")
public static JSONObject getMarkerTooltip(ModuleBaseWithCustomFields record, V3MarkerContext marker,Long markerModuleId, Context context, V3IndoorFloorPlanPropertiesContext properties) throws Exception {

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
					 tooltipCoreData.put("label", desk.getDeskTypeName());
					 tooltipConent.add(tooltipCoreData);

					 
					 if (desk.getEmployee() != null) {
						 
						 tooltipCoreData = new JSONObject();
						 tooltipCoreData.put("icon", "employee");
						 tooltipCoreData.put("label", desk.getEmployee().getName());
						 tooltipConent.add(tooltipCoreData);

					 }
					 else {
						 tooltipCoreData = new JSONObject();

						 
						 if (desk.getDeskType() == 1) {
							 tooltipCoreData.put("icon", "employee");
							 tooltipCoreData.put("label", "Unassigned");
						 }

					
		
						 tooltipConent.add(tooltipCoreData);
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
 
	public static JSONObject getZoneTooltipData(V3MarkerdZonesContext zone, Context context) throws Exception {

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
	
	


}