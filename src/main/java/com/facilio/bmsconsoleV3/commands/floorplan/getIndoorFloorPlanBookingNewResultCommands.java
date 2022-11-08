package com.facilio.bmsconsoleV3.commands.floorplan;


import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsoleV3.context.V3DepartmentContext;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

import static com.facilio.bmsconsoleV3.util.V3FloorPlanAPI.checkViewAssignmentPermission;
import static com.facilio.bmsconsoleV3.util.V3FloorPlanAPI.checkViewAssignmentUserPermission;


@Log4j
public class getIndoorFloorPlanBookingNewResultCommands extends FacilioCommand {

	// this command is used to set the booking result into floorplan properties context

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

		Role role = AccountUtil.getCurrentUser().getRole();
		if (!role.isPrevileged()) {
			context.put("bookingMarkerObject", (JSONObject) V3FloorPlanAPI.getBookingMarkerProperties());
		}



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

	public static V3IndoorFloorPlanPropertiesContext getMarkerProperties(ModuleBaseWithCustomFields record, V3MarkerContext marker, Long markerModuleId, Context context) throws Exception {


		Map<Long, List<V3SpaceBookingContext>> spaceBookingMap = (Map<Long, List<V3SpaceBookingContext>>) context.get(FacilioConstants.ContextNames.Floorplan.SPACE_BOOKING_MAP);

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
				properties.setSpaceCategoryId(desk.getSpaceCategoryId());


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
					if (!CollectionUtils.sizeIsEmpty(spaceBookingMap)) {
						List<V3SpaceBookingContext> deskBookingList = (List<V3SpaceBookingContext>) spaceBookingMap.get(desk.getId());

						if (deskBookingList != null && deskBookingList.size() > 0) {
							properties.setSecondaryLabel(V3FloorPlanAPI.getSecondaryLabelFromSpacebooking(deskBookingList));
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


				 String customString = bookingCustomization.getDeskSecondaryLabel().getCustomText();
				   bookingCustomization.getDeskSecondaryLabel().getLabelType().setCustomText(customString);
				   properties.setLabel(bookingCustomization.getDeskSecondaryLabel().getLabelType().format(desk));

				   customString = bookingCustomization.getDeskPrimaryLabel().getCustomText();
				   bookingCustomization.getDeskPrimaryLabel().getLabelType().setCustomText(customString);
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
		JSONObject mark = (JSONObject) context.get("bookingMarkerObject");
		V3EmployeeContext employee = (V3EmployeeContext)mark.get("employee");
		V3DeskContext desk = (V3DeskContext) record;

		Map<Long, List<V3SpaceBookingContext>> spaceBookingMap = (Map<Long, List<V3SpaceBookingContext>>) context.get(FacilioConstants.ContextNames.Floorplan.SPACE_BOOKING_MAP);

		try {
			if (desk.getDeskType() == 1) {
				checkViewAssignmentPermission(properties, employeemarker, deskdepartment, record, context);
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
						if (!CollectionUtils.sizeIsEmpty(spaceBookingMap)) {
							List<V3SpaceBookingContext> bookingList = (List<V3SpaceBookingContext>) spaceBookingMap.get(desk.getId());

							bookingList.forEach(booking -> {
								if (booking != null) {
									if (booking.getHost() != null && booking.getHost().getName() != null) {
										if (booking.getHost().getId() != employee.getId()) {
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

		return checkBookingPermission(tooltip,record, context);
	}
	public static JSONObject checkBookingPermission(JSONObject tooltip,ModuleBaseWithCustomFields record, Context context) throws Exception {
		Role role = AccountUtil.getCurrentUser().getRole();
		if (role.isPrevileged()) {
			return tooltip;
		}
		V3DeskContext desk = (V3DeskContext) record;
		JSONObject marktooltip = (JSONObject) context.get("bookingMarkerObject");
		V3EmployeeContext employee = (V3EmployeeContext) marktooltip.get("employee");
		try {
			if (desk.getDeskType() == 1) {
				checkViewAssignmentUserPermission(tooltip,record, context);
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
