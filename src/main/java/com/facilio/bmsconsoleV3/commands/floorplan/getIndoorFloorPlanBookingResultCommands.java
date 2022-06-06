package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.context.V3DepartmentContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.facilio.bmsconsoleV3.util.V3FloorPlanAPI.checkViewAssignmentPermission;
import static com.facilio.bmsconsoleV3.util.V3FloorPlanAPI.checkViewAssignmentUserPermission;

@Log4j
public class getIndoorFloorPlanBookingResultCommands extends FacilioCommand {
	public static JSONObject getMarkerPropertiesObject() throws Exception {
		JSONObject markerObject = new JSONObject();
		HttpServletRequest request = ServletActionContext.getRequest();
		String currentTab = request.getHeader("X-Tab-Id");
		long tabId = Long.valueOf(currentTab);
		Role role = AccountUtil.getCurrentUser().getRole();
		long ouid = AccountUtil.getCurrentAccount().getUser().getOuid();
		String moduleName = "INDOOR_FLOOR_PLAN";
		V3EmployeeContext employee = V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, ouid, V3EmployeeContext.class);
		boolean hasAllAccess = PermissionUtil.userHasPermission(tabId, moduleName, "VIEW_BOOKING", role);
		boolean hasDepartmentAccess = PermissionUtil.userHasPermission(tabId, moduleName, "VIEW_BOOKING_DEPARTMENT", role);
		boolean hasOwnAccess = PermissionUtil.userHasPermission(tabId, moduleName, "VIEW_BOOKING_OWN", role);
		markerObject.put("currentTab", currentTab);
		markerObject.put("tabId",tabId);
		markerObject.put("role", role);
		markerObject.put("ouid",ouid );
		markerObject.put("hasAllAccess", hasAllAccess);
		markerObject.put("hasDepartmentAccess", hasDepartmentAccess);
		markerObject.put("hasOwnAccess", hasOwnAccess);
		markerObject.put("employee",employee);
		return markerObject;
	}

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

		List<V3IndoorFloorPlanGeoJsonContext> geoMarkers = new ArrayList<>();
		List<V3IndoorFloorPlanGeoJsonContext> geoZones = new ArrayList<>();


		if (viewMode == null) {
			viewMode = FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW;
		}

		try {
			Map<String, FacilioForm> froms = FormsAPI.getFormsAsMap(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, false, false, AccountUtil.getCurrentApp().getId());
			context.put(FacilioConstants.ContextNames.FORMS, froms);
		}
		catch (Exception e) {
			LOGGER.error("Error while fetching forms", e);
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
					geoMarkers.add(feature);
		        	
		        	
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
				 	geoZones.add(feature);
		   
		        }
		          
		}
        
       
       
        context.put(FacilioConstants.ContextNames.Floorplan.MARKERS, markerObjectMap);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONES, zoneObjectMap);
		context.put("GEO_MARKERS", geoMarkers);
		context.put("GEO_ZONES", geoZones);

        
		return false;
	}
	
	public static V3IndoorFloorPlanPropertiesContext getMarkerProperties(ModuleBaseWithCustomFields record, V3MarkerContext marker,Long markerModuleId, Context context) throws Exception {
		Map<String, FacilioForm> forms = (Map<String, FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS);




		Map<Long, List<BookingSlotsContext>> facilityBookingsMap = (Map<Long, List<BookingSlotsContext>>) context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);

			Map<Long, V3FacilityBookingContext> bookingMap =  (Map<Long, V3FacilityBookingContext>) context.get("bookingMap");

        V3IndoorFloorPlanPropertiesContext properties = new V3IndoorFloorPlanPropertiesContext();
		V3EmployeeContext bookingemployeemarker = null;
		V3DepartmentContext bookingdeskdepartment = null;
	   
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

			   if (forms != null && CollectionUtils.isNotEmpty(Collections.singleton(forms))) {
				   FacilioForm hotDeskBookiingForm = forms.get("hot_desk_facilitybooking_web");
				   FacilioForm bookingForm = forms.get("default_facilitybooking_web");
				   if (hotDeskBookiingForm != null) {
					   properties.setBookingFormId(hotDeskBookiingForm.getId());
				   }
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
			   bookingemployeemarker = desk.getEmployee();
			   bookingdeskdepartment = desk.getDepartment();

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
	   return checkUserBookingPermission(properties, bookingemployeemarker, bookingdeskdepartment,context,record);
	   
  }
	public static V3IndoorFloorPlanPropertiesContext checkUserBookingPermission(V3IndoorFloorPlanPropertiesContext properties,V3EmployeeContext employeemarker, V3DepartmentContext deskdepartment,Context context,ModuleBaseWithCustomFields record) throws Exception {
		Role role = AccountUtil.getCurrentUser().getRole();
		if (role.isPrevileged()) {
			return properties;
		}
		JSONObject mark = (JSONObject) getMarkerPropertiesObject();
		V3EmployeeContext employee = (V3EmployeeContext)mark.get("employee");
		V3DeskContext desk = (V3DeskContext) record;

		Map<Long, List<BookingSlotsContext>> facilityBookingsMap = (Map<Long, List<BookingSlotsContext>>) context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);

		Map<Long, V3FacilityBookingContext> bookingMap =  (Map<Long, V3FacilityBookingContext>) context.get("bookingMap");

		try {
			if (desk.getDeskType() == 1) {
				checkViewAssignmentPermission(properties, employeemarker, deskdepartment, record);
			} else {
				if ((Boolean) mark.get("hasAllAccess")) {
					//
				} else if ((Boolean) mark.get("hasDepartmentAccess")) {
					if (employeemarker != null) {
						if(properties.getDepartmentId()!=employee.getDepartment().getId()){
							hideMarkerProperties(properties);
						}
					}
				} else if ((Boolean) mark.get("hasOwnAccess")) {
					if (employeemarker != null) {
						if(properties.getEmployeeId()!=employee.getId()){
							hideMarkerProperties(properties);
						}
					} 
					else{
						if (!CollectionUtils.sizeIsEmpty(facilityBookingsMap)) {
							List<BookingSlotsContext> facilityBooking = (List<BookingSlotsContext>) facilityBookingsMap.get(desk.getId());

							facilityBooking.forEach(r -> {
								if (r.getBooking() != null) {
									V3FacilityBookingContext booking = bookingMap.get(r.getBooking().getId());
									if (booking.getReservedFor() != null && booking.getReservedFor().getName() != null) {
										if (booking.getReservedFor().getId() != employee.getId()) {
											hideMarkerProperties(properties);
										}
									}
								}
							});
						}
					}
				} else {
					// hide all properties
					hideMarkerProperties(properties);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}


	public static void hideMarkerProperties(V3IndoorFloorPlanPropertiesContext properties) {
		properties.setCenterLabel("");
		properties.setSecondaryLabel("");
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
		
         return checkBookingPermission(tooltip,record);
     }
	public static JSONObject checkBookingPermission(JSONObject tooltip,ModuleBaseWithCustomFields record) throws Exception {
		Role role = AccountUtil.getCurrentUser().getRole();
		if (role.isPrevileged()) {
			return tooltip;
		}
		V3DeskContext desk = (V3DeskContext) record;
		JSONObject marktooltip = (JSONObject) getMarkerPropertiesObject();
		V3EmployeeContext employee = (V3EmployeeContext) marktooltip.get("employee");
		try {
			if (desk.getDeskType() == 1) {
				checkViewAssignmentUserPermission(tooltip,record);
			}
			else {
				if ((Boolean) marktooltip.get("hasAllAccess")) {
					//
				} 
				else if ((Boolean) marktooltip.get("hasDepartmentAccess")) {
					if (desk.getEmployee() != null) {
						if (desk.getEmployee().getDepartment().getId() != employee.getDepartment().getId() ){
							hideTooltipData(tooltip);
						}
					}
				} else if ((Boolean) marktooltip.get("hasOwnAccess")) {
					if (desk.getEmployee() != null) {
						if (desk.getEmployee().getId() != employee.getId()) {
							hideTooltipData(tooltip);
						}
					}
				} else {
					// hide all properties
					hideTooltipData(tooltip);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return tooltip;
	}
	public static void hideTooltipData(JSONObject tooltip){
		tooltip.put("content", "");
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