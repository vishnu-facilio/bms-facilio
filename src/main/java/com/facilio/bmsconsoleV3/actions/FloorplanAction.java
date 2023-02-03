package com.facilio.bmsconsoleV3.actions;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanGeoJsonContext;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

public class FloorplanAction extends V3Action {

	private static final long serialVersionUID = 1L;

	public Boolean getNewBooking() {
		if (newBooking == null) {
			return false;
		}
		return newBooking;
	}

	public void setNewBooking(Boolean newBooking) {
		this.newBooking = newBooking;
	}

	private Boolean newBooking;
	
	private List<Long> spaceIds;
	public List<Long> getSpaceIds() {
		return spaceIds;
	}
	public void setSpaceIds(List<Long> spaceIds) {
		this.spaceIds = spaceIds;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private long floorId = -1;
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}
	
	private long floorPlanId = -1;
	public long getFloorPlanId() {
		return floorPlanId;
	}
	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String search;
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	
	private long getFloorplanId() {
		return floorplanId;
	}
	public void setFloorplanId(long floorplanId) {
		this.floorplanId = floorplanId;
	}

	private long floorplanId;
	
	public String getViewMode() {
		return viewMode;
	}
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	private String viewMode;


	public List<Long> getObjectIds() {
		return objectIds;
	}
	public void setObjectIds(List<Long> objectIds) {
		this.objectIds = objectIds;
	}

	private List<Long> objectIds;
	
	
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	private Long objectId;


	public FloorplanFilterContext getFloorplanFilters() {
		return floorplanFilters;
	}

	public void setFloorplanFilters(FloorplanFilterContext floorplanFilters) {
		this.floorplanFilters = floorplanFilters;
	}


	public static class FloorplanFilterContext {
		public List<Long> getAmenities() {
			return amenities;
		}

		public void setAmenities(List<Long> amenities) {
			this.amenities = amenities;
		}

		private List<Long> amenities;
	}
	private FloorplanFilterContext floorplanFilters;
	
	
	public String getFacilityDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactoryV3.getFloorplanFacilitiesChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.SPACE_LIST, spaceIds);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		
		chain.execute();
		
		setData(FacilioConstants.ContextNames.FacilityBooking.FACILITY, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
		
		setData(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
		
		return SUCCESS;
	}
	
	public String getFloorplanDetailsByType() throws Exception {
		if(floorId > 0) {
			FacilioChain chain = ReadOnlyChainFactoryV3.getFloorplanMapByTypeChain();
			
			FacilioContext context = chain.getContext();
			
			context.put(FacilioConstants.ContextNames.FLOOR, floorId);
			
			chain.execute();
			setData(FacilioConstants.ContextNames.INDOOR_FLOOR_PLANS, context.get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLANS));
		}
		return SUCCESS;
	}
	
	public String floorplanListSearch() throws Exception {
		FacilioChain chain = ReadOnlyChainFactoryV3.floorplanListSearchChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE, moduleName);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.SEARCH, search);
		context.put(FacilioConstants.ContextNames.FLOOR, floorId);
		context.put(FacilioConstants.ContextNames.FLOOR_PLAN_ID,floorPlanId);
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		
		chain.execute();
		
		setData(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.RECORD_LIST));
		return SUCCESS;
	}
	public String getViewData() throws Exception {

		if (viewMode != null && viewMode.equals(FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW)) {
			FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanViewerObjectChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID, floorplanId);
			context.put(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS, objectIds);
			context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);

			chain.execute();

			List<V3IndoorFloorPlanGeoJsonContext> geoMarkers = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_MARKERS");
			List<V3IndoorFloorPlanGeoJsonContext> geoZones = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_ZONES");
			setData("marker", V3FloorPlanAPI.convertGeoJson(geoMarkers));
			setData("spaceZone", V3FloorPlanAPI.convertGeoJson(geoZones));
			//setData(FacilioConstants.ContextNames.Floorplan.MARKERS, context.get(FacilioConstants.ContextNames.Floorplan.MARKERS));
			//setData(FacilioConstants.ContextNames.Floorplan.ZONES, context.get(FacilioConstants.ContextNames.Floorplan.ZONES));
			setData(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN));
			setData(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, context.get(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER));
			setData("floorplanMappedmodules", context.get("FLOORPLAN_MAPPED_MODULEOBJECT"));

		}
		else if (viewMode != null && viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW)) {


			FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanBookingObjectChain();


			if (getNewBooking() == true) {
				chain = ReadOnlyChainFactoryV3.getfloorplanNewBookingObjectChain();
			}
			FacilioContext context = chain.getContext();

			context.put(FacilioConstants.ContextNames.START_TIME, startTime);
			context.put(FacilioConstants.ContextNames.END_TIME, endTime);
			context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID, floorplanId);
			context.put(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS, objectIds);
			context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);

			List<Long> amenities = new ArrayList<>();

			if(floorplanFilters !=null) {
				amenities = floorplanFilters.getAmenities();
			}
			context.put(FacilioConstants.ContextNames.FacilityBooking.AMENITY, amenities);

			context.put("IS_NEW_BOOKING", getNewBooking());
			chain.execute();

			setData(FacilioConstants.ContextNames.FacilityBooking.FACILITY, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
			setData(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
			setData("bookingList", context.get("bookingMap"));

			List<V3IndoorFloorPlanGeoJsonContext> geoMarkers = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_MARKERS");
			List<V3IndoorFloorPlanGeoJsonContext> geoZones = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_ZONES");

			setData("marker", V3FloorPlanAPI.convertGeoJson(geoMarkers));
			setData("spaceZone", V3FloorPlanAPI.convertGeoJson(geoZones));
		//	setData(FacilioConstants.ContextNames.Floorplan.MARKERS, context.get(FacilioConstants.ContextNames.Floorplan.MARKERS));
		//	setData(FacilioConstants.ContextNames.Floorplan.ZONES, context.get(FacilioConstants.ContextNames.Floorplan.ZONES));
			setData(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN));
			setData(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, context.get(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER));
			setData("floorplanMappedmodules", context.get("FLOORPLAN_MAPPED_MODULEOBJECT"));

		}
		else {
			FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanViewerObjectChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID, floorplanId);
			context.put(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS, objectIds);
			chain.execute();

			List<V3IndoorFloorPlanGeoJsonContext> geoMarkers = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_MARKERS");
			List<V3IndoorFloorPlanGeoJsonContext> geoZones = (List<V3IndoorFloorPlanGeoJsonContext>) context.get("GEO_ZONES");

			setData("marker", V3FloorPlanAPI.convertGeoJson(geoMarkers));
			setData("spaceZone", V3FloorPlanAPI.convertGeoJson(geoZones));
			//setData(FacilioConstants.ContextNames.Floorplan.MARKERS, context.get(FacilioConstants.ContextNames.Floorplan.MARKERS));
			//setData(FacilioConstants.ContextNames.Floorplan.ZONES, context.get(FacilioConstants.ContextNames.Floorplan.ZONES));
			setData(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN));
			setData(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, context.get(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER));
			setData("floorplanMappedmodules", context.get("FLOORPLAN_MAPPED_MODULEOBJECT"));

		}


		return SUCCESS;
	}
	public String getFloorplanViewData() throws Exception {

		if (viewMode != null && viewMode.equals(FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW)) {
			FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanViewerObjectChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID, floorplanId);
			context.put(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS, objectIds);
			context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);
			chain.execute();		
			//setData(FacilioConstants.ContextNames.Floorplan.MARKERS, context.get(FacilioConstants.ContextNames.Floorplan.MARKERS));
			setData(FacilioConstants.ContextNames.Floorplan.ZONES, context.get(FacilioConstants.ContextNames.Floorplan.ZONES));
			setData(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN));
			setData(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, context.get(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER));


		}
		else if (viewMode != null && viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW)) {
			
			FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanBookingObjectChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.START_TIME, startTime);
			context.put(FacilioConstants.ContextNames.END_TIME, endTime);
			context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID, floorplanId);
			context.put(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS, objectIds);
			context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);
			chain.execute();
			
			setData(FacilioConstants.ContextNames.FacilityBooking.FACILITY, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY));
			setData(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, context.get(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING));
			setData("bookingList", context.get("bookingMap"));

			
			//setData(FacilioConstants.ContextNames.Floorplan.MARKERS, context.get(FacilioConstants.ContextNames.Floorplan.MARKERS));
			//setData(FacilioConstants.ContextNames.Floorplan.ZONES, context.get(FacilioConstants.ContextNames.Floorplan.ZONES));
			setData(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN));
			setData(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, context.get(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER));

		}
		else {
			FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanViewerObjectChain();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID, floorplanId);
			context.put(FacilioConstants.ContextNames.Floorplan.OBJECT_IDS, objectIds);
			chain.execute();	
			
		//	setData(FacilioConstants.ContextNames.Floorplan.MARKERS, context.get(FacilioConstants.ContextNames.Floorplan.MARKERS));
			//setData(FacilioConstants.ContextNames.Floorplan.ZONES, context.get(FacilioConstants.ContextNames.Floorplan.ZONES));
			setData(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN));
			setData(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER, context.get(FacilioConstants.ContextNames.Floorplan.FLOORPLAN_CLIENT_LAYER));

		}
		return SUCCESS;
	}
	
	public String getPropertiesData() throws Exception {
		
		if (objectId != null && objectId > 0) {
			
			if (viewMode != null && viewMode.equals(FacilioConstants.ContextNames.Floorplan.ASSIGNMENT_VIEW)) {

				FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanPropertiesChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.Floorplan.OBJECTID, objectId);
				context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);

				chain.execute();
				
				setData(FacilioConstants.ContextNames.Floorplan.PROPERTIES, context.get(FacilioConstants.ContextNames.Floorplan.PROPERTIES));

				
			}
			else if (viewMode != null && viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW)) {

				FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanBookingPropertiesChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.Floorplan.OBJECTID, objectId);
				context.put(FacilioConstants.ContextNames.START_TIME, startTime);
				context.put(FacilioConstants.ContextNames.END_TIME, endTime);
				context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);

				chain.execute();
				setData(FacilioConstants.ContextNames.Floorplan.PROPERTIES, context.get(FacilioConstants.ContextNames.Floorplan.PROPERTIES));

				
			}
			else {
				
				FacilioChain chain = ReadOnlyChainFactoryV3.getfloorplanPropertiesChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.Floorplan.OBJECTID, objectId);
				context.put(FacilioConstants.ContextNames.Floorplan.VIEW_MODE, viewMode);

				chain.execute();
				
				setData(FacilioConstants.ContextNames.Floorplan.PROPERTIES, context.get(FacilioConstants.ContextNames.Floorplan.PROPERTIES));

				
			}

			
			
		}
		else {
			setData(FacilioConstants.ContextNames.Floorplan.PROPERTIES, new JSONObject());
		}

		
		return SUCCESS;
	}
	
	
	
}
