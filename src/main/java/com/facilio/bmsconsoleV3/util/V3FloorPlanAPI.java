package com.facilio.bmsconsoleV3.util;

import java.util.*;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.google.api.client.json.Json;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.json.JSONString;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
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
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;

import static com.facilio.constants.FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN;
import static java.util.Objects.isNull;
import static nl.basjes.shaded.org.springframework.util.ObjectUtils.isEmpty;


public class V3FloorPlanAPI {

	public static Map<String,JSONObject> convertGeoJson(List<V3IndoorFloorPlanGeoJsonContext> fetaure) throws Exception {
		JSONObject GeoJson = new JSONObject();
		GeoJson.put("type", "FeatureCollection");
		GeoJson.put("features", fetaure);
		return GeoJson;
	}
	public static JSONObject getMarkerObject() throws Exception {
		JSONObject markerObject = new JSONObject();
		HttpServletRequest	 request = ServletActionContext.getRequest();
		String currentTab = request.getHeader("X-Tab-Id");
		long tabId = Long.valueOf(currentTab);
		Role role = AccountUtil.getCurrentUser().getRole();
		long id = AccountUtil.getCurrentUser().getUid();
		String moduleName = "INDOOR_FLOOR_PLAN";
		V3EmployeeContext emp = V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE,id,V3EmployeeContext.class);
		boolean hasAllAccess = PermissionUtil.userHasPermission(tabId, moduleName, "VIEW_ASSIGNMENT", role);
		boolean hasDepartmentAccess = PermissionUtil.userHasPermission(tabId, moduleName, "VIEW_ASSIGNMENT_DEPARTMENT", role);
		boolean hasOwnAccess = PermissionUtil.userHasPermission(tabId, moduleName, "VIEW_ASSIGNMENT_OWN", role);
		boolean hasViewAccess = PermissionUtil.userHasPermission(tabId,moduleName,"VIEW",role);
		markerObject.put("currentTab", currentTab);
		markerObject.put("tabId",tabId);
		markerObject.put("role", role);
		markerObject.put("userId",id );
		markerObject.put("hasAllAccess", hasAllAccess);
		markerObject.put("hasDepartmentAccess", hasDepartmentAccess);
		markerObject.put("hasOwnAccess", hasOwnAccess);
		markerObject.put("hasViewAccess",hasViewAccess);
		markerObject.put("employee",emp);
		return markerObject;
	}


	public static List<V3IndoorFloorPlanContext> getAllFloorPlan() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

		SelectRecordsBuilder<V3IndoorFloorPlanContext> builder = new SelectRecordsBuilder<V3IndoorFloorPlanContext>()
				.module(module)
				.beanClass(V3IndoorFloorPlanContext.class)
				.select(modBean.getAllFields(module.getName()));

		List<V3IndoorFloorPlanContext> floorplans = builder.get();
		if(CollectionUtils.isNotEmpty(floorplans))
		{
			return floorplans;
		}
		return new ArrayList<>();
		}
		public static List<V3IndoorFloorPlanLayerContext> getFloorplanLayer(Long floorplanId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_LAYER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

		SelectRecordsBuilder<V3IndoorFloorPlanLayerContext> builder = new SelectRecordsBuilder<V3IndoorFloorPlanLayerContext>()
				.module(module)
				.beanClass(V3IndoorFloorPlanLayerContext.class)
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getCondition("INDOOR_FLOORPLAN_ID", "indoorfloorplanid", String.valueOf(floorplanId), NumberOperators.EQUALS))
						 .orderBy("LAYER_TYPE ASC");

		List<V3IndoorFloorPlanLayerContext> layers = builder.get();
		if(CollectionUtils.isNotEmpty(layers))
		{
			return layers;
		}
		return new ArrayList<>();
	}
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
					 tooltipCoreData.put("label", desk.getDeskTypeName());
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
         return checkViewAssignmentUserPermission(tooltip,record);
     }

	public static JSONObject checkViewAssignmentUserPermission(JSONObject tooltip,ModuleBaseWithCustomFields record) throws Exception {
		Role role = AccountUtil.getCurrentUser().getRole();
		if (role.isPrevileged()) {
			return tooltip;
		}

			V3DeskContext desk = (V3DeskContext) record;
			JSONObject marktooltip = (JSONObject) getMarkerObject();
			V3EmployeeContext employee = (V3EmployeeContext) marktooltip.get("employee");
			try {
				if (!((Boolean) marktooltip.get("hasViewAccess"))) {
					hideTooltipDefaultProperties(tooltip);
				} else {
					if ((Boolean) marktooltip.get("hasAllAccess")) {
						//
					} else if ((Boolean) marktooltip.get("hasDepartmentAccess")) {
						if (desk.getEmployee() != null) {
							if (desk.getEmployee().getDepartment().getId() != employee.getDepartment().getId() ){

								hideTooltipProperties(tooltip);
							}
						}
					} else if ((Boolean) marktooltip.get("hasOwnAccess")) {
						if (desk.getEmployee() != null) {
							if (desk.getEmployee().getId() != employee.getId()) {
								hideTooltipProperties(tooltip);
							}
						}
					} else {
						// hide all properties
						hideTooltipDefaultProperties(tooltip);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		return tooltip;
	}
	public static void hideTooltipProperties(JSONObject tooltip){

		List<JSONObject> toolTipList = (List<JSONObject>) tooltip.get("content");
		for (JSONObject toolTipElement : toolTipList){
			if (toolTipElement.get("icon").equals("employee")){
				toolTipElement.put("label", "occupied by someone");
			}
		}
	}
	public static void hideTooltipDefaultProperties(JSONObject tooltip){
		tooltip.put("content","");
	}

			 public static V3IndoorFloorPlanPropertiesContext getZoneProperties(ModuleBaseWithCustomFields record, V3MarkerdZonesContext zone, Context context, String viewMode,Long markerModuleId) throws Exception {
			  Map<Long, List<BookingSlotsContext>> facilityBookingsMap = (Map<Long, List<BookingSlotsContext>>) context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);

			  V3IndoorFloorPlanPropertiesContext properties = new V3IndoorFloorPlanPropertiesContext();
			  V3FloorplanCustomizationContext assignCustomization = (V3FloorplanCustomizationContext) context.get("FLOORPLAN_ASSIGNMENT_CUSTOMIZATION");
			  V3FloorplanCustomizationContext bookingCustomization = (V3FloorplanCustomizationContext) context.get("FLOORPLAN_BOOKING_CUSTOMIZATION");
			  String spaceColor = assignCustomization.getSpaceBookingState().getNonReservableColor();
			  String reservedColor = bookingCustomization.getBookingState().getNotAvailableColor();
			  String availableColor = bookingCustomization.getBookingState().getAvailableColor();
			  String nonReservableColor = bookingCustomization.getBookingState().getNonReservableColor();
			  String partiallyAvailableColor = bookingCustomization.getBookingState().getPartiallyAvailableColor();

	 			   properties.setObjectId(zone.getId());
	 			   properties.setRecordId(zone.getRecordId());
	 			if (zone.getSpace() != null) {
	 				properties.setSpaceId(zone.getSpace().getId());
					if (viewMode.equals(FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW)) {
						properties.setLabel(assignCustomization.getSpacePrimaryLabel().getLabelType().format(zone.getSpace()));
						properties.setSecondaryLabel(assignCustomization.getSpaceSecondaryLabel().getLabelType().format(zone.getSpace()));
						properties.setZoneBackgroundColor(spaceColor);
					}
					else if(viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW))
					{
						properties.setLabel(bookingCustomization.getSpacePrimaryLabel().getLabelType().format(zone.getSpace()));
						properties.setSecondaryLabel(bookingCustomization.getSpaceSecondaryLabel().getLabelType().format(zone.getSpace()));
						properties.setZoneBackgroundColor(nonReservableColor);
					}

	 				if (zone.getSpace().getSpaceCategory() != null && zone.getSpace().getSpaceCategory().getName() != null) {
	 					properties.setSpaceCategory(zone.getSpace().getSpaceCategory().getName());

	 				}

	 				properties.setIsOccupied(false);

					Map<String, FacilioForm> forms = (Map<String, FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS);

					if (forms != null && CollectionUtils.isNotEmpty(Collections.singleton(forms))) {
						FacilioForm parkingBooking = forms.get("default_parkingbooking_web");
						FacilioForm spaceBookingform = forms.get("default_facilitybooking_web");
						String SpaceCategory = "Parking Stall";
						if (properties.getSpaceCategory().equals(SpaceCategory)) {
							if (parkingBooking != null) {
								properties.setBookingFormId(parkingBooking.getId());
							}
						}
						else {
							if (spaceBookingform != null) {
								properties.setBookingFormId(spaceBookingform.getId());
							}
						}

					}
	 				
	 			
	 				if (markerModuleId != null && record != null) {
	 					
	 					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	 			        FacilioModule module = modBean.getModule(markerModuleId);
	 	 			   properties.setMarkerModuleId(zone.getZoneModuleId());
	 			        properties.setMarkerModuleName(module.getName());
	 					
	 					if (module.getName().equals(FacilioConstants.ContextNames.LOCKERS)) {
	 						
	 						V3LockersContext locker = (V3LockersContext) record;
	 						
	 						if (locker.getEmployee() != null) {
	 							properties.setIsOccupied(true);
	 							properties.setEmployeeId(locker.getEmployee().getId());
	 							properties.setZoneBackgroundColor(reservedColor);
	 						}
	 						else {
	 							properties.setZoneBackgroundColor(availableColor);
	 						}
	 						
	 						
	 						
	 					}
	 					else if (module.getName().equals(FacilioConstants.ContextNames.PARKING_STALL)) {
	 						V3ParkingStallContext parking = (V3ParkingStallContext) record;
	 						

	 						
	 						if (parking.getModuleState() != null) {
	 				        	FacilioStatus status = TicketAPI.getStatus(module, "Occupied");
	 				        	FacilioStatus vacantStatus = TicketAPI.getStatus(module, "Vacant");

	 				        	
	 							if (status != null && parking.getModuleState().getId() == status.getId()) {
	 								properties.setIsOccupied(true);
	 								properties.setZoneBackgroundColor(reservedColor);
	 							}
	 							else if (vacantStatus != null && parking.getModuleState().getId() == vacantStatus.getId()) {
	 								properties.setZoneBackgroundColor(availableColor);
	 							}
	 						}
	 						
	 					}
	 					
	 				    if (viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW)) {
	 				    	if (zone.isIsReservable()) {
		 						properties.setZoneBackgroundColor(availableColor);
		 					}

							if (MapUtils.isNotEmpty(facilityBookingsMap) && zone.getSpace().getId() > 0) {
								List<BookingSlotsContext> facilityBooking = (List<BookingSlotsContext>) facilityBookingsMap.get(zone.getSpace().getId());

								if (CollectionUtils.isNotEmpty(facilityBooking)) {
									properties.setZoneBackgroundColor(reservedColor);
									properties.setIsBooked(true);
								}

							}
	 				   }
	 					
	 					
	 					
	 				}			
	 			
	 				
	 			}
	 			else {
	 			    properties.setLabel(zone.getLabel());

	 			}

	 			return properties;
	 		   
	 	   }


	   public static V3IndoorFloorPlanPropertiesContext getMarkerProperties(ModuleBaseWithCustomFields record, V3MarkerContext marker,Long markerModuleId, String viewMode, Context context) throws Exception {

		   V3IndoorFloorPlanPropertiesContext properties = new V3IndoorFloorPlanPropertiesContext();

		   V3EmployeeContext employeemarker = null;
		   V3DepartmentContext deskdepartment = null;
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

			   if (module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
				   // to handle desk details here

				   V3DeskContext desk = (V3DeskContext) record;
				   V3FloorplanCustomizationContext assignCustomization = (V3FloorplanCustomizationContext) context.get("FLOORPLAN_ASSIGNMENT_CUSTOMIZATION");
				   String iconName = "desk";
				   properties.setDeskCode(desk.getDeskCode());
				   properties.setDeskId(desk.getId());
				   properties.setDeskType(desk.getDeskType());
				   properties.setLabel(assignCustomization.getDeskSecondaryLabel().getLabelType().format(desk));
				   properties.setSecondaryLabel(assignCustomization.getDeskPrimaryLabel().getLabelType().format(desk));
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
						   properties.setCenterLabel(getCenterLabel(desk.getEmployee().getName()));
						   properties.setIconName(iconName);

						   iconName = iconName.concat("_" + desk.getDeskTypeName());


						   if (desk.getDepartment() != null) {
							   iconName = iconName.concat("_" + desk.getDepartment().getName());
						   }

					   }
					   employeemarker = desk.getEmployee();
					   deskdepartment = desk.getDepartment();
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
						   properties.setIconName(iconName);
						   properties.setSecondaryLabel(desk.getEmployee().getName());
						   // set booking desk icon name

					   }

					   // properties.setBookingFormId(getBookingFormIdFromModule);

				   } 
				   else {
					   if (desk.getDepartment() != null) {
						   properties.setDepartmentId(desk.getDepartment().getId());
					   }

					   if (desk.getEmployee() != null) {
						   properties.setEmployeeId(desk.getEmployee().getId());
						   properties.setSecondaryLabel(desk.getEmployee().getName());
						   properties.setIconName(iconName);

						   iconName = iconName.concat("_" + desk.getDeskTypeName());


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
			   } else {
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

		   return checkViewAssignmentPermission(properties, employeemarker, deskdepartment,record);

	   }

	public static V3IndoorFloorPlanPropertiesContext checkViewAssignmentPermission(V3IndoorFloorPlanPropertiesContext properties,V3EmployeeContext employeemarker, V3DepartmentContext deskdepartment, ModuleBaseWithCustomFields record) throws Exception {
		Role role = AccountUtil.getCurrentUser().getRole();
		if (role.isPrevileged()) {
			return properties;
		}
		JSONObject mark = (JSONObject) getMarkerObject();
		V3EmployeeContext employee = (V3EmployeeContext)mark.get("employee");
		V3DeskContext desk = (V3DeskContext) record;
		try {
			if (!((Boolean) mark.get("hasViewAccess"))) {
				hideProperties(properties);
			}
			else{
			if (desk.getDeskType() == 3) {
				hideProperties(properties);
			} else {

				if ((Boolean) mark.get("hasAllAccess")) {
					//
				} else if ((Boolean) mark.get("hasDepartmentAccess")) {
					if (employeemarker != null) {
						if(properties.getDepartmentId()!=employee.getDepartment().getId()){
							hideProperties(properties);
						}
					}
				} else if ((Boolean) mark.get("hasOwnAccess")) {
					if (employeemarker != null) {
						if(properties.getEmployeeId()!=employee.getId()){
							hideProperties(properties);
						}
					}
					}

				 else {
					// hide all properties
					hideProperties(properties);
				}
			}


				}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
	public static void hideProperties(V3IndoorFloorPlanPropertiesContext properties){

		properties.setActive(false);
		properties.setCenterLabel("");
		properties.setSecondaryLabel("");
		properties.setOpenDialog(false);

	}

	   public static String getCenterLabel(String name) {
		   String result = "";
		   
		   // have to workout
		   
		   if (name != null) {
			   String[] splitStr = name.trim().split("\\s+");
			   
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
		   
		   return result;

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
	        builder.fetchSupplement((LookupField) fieldMap.get("markerType"));

	        
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
