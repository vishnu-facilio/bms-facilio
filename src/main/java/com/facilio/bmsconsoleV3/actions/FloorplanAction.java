package com.facilio.bmsconsoleV3.actions;

import java.util.List;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;



import lombok.Getter;
import lombok.Setter;

public class FloorplanAction extends V3Action {

	private static final long serialVersionUID = 1L;
	
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
}
