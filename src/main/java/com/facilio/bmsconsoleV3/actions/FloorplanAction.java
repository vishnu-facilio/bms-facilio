package com.facilio.bmsconsoleV3.actions;

import java.util.List;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
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
	
	private String filters;
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
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
		context.put(FacilioConstants.ContextNames.SEARCH, search);
		context.put(FacilioConstants.ContextNames.FLOOR, floorId);
		context.put(FacilioConstants.ContextNames.FILTERS, filters);
		
		chain.execute();
		
		setData(FacilioConstants.ContextNames.EMPLOYEE, context.get(FacilioConstants.ContextNames.EMPLOYEE));
		setData(FacilioConstants.ContextNames.Floorplan.DESKS, context.get(FacilioConstants.ContextNames.Floorplan.DESKS));
		setData(FacilioConstants.ContextNames.SPACE, context.get(FacilioConstants.ContextNames.SPACE));
		setData(FacilioConstants.ContextNames.LOCKERS, context.get(FacilioConstants.ContextNames.LOCKERS));
		setData(FacilioConstants.ContextNames.PARKING_STALL, context.get(FacilioConstants.ContextNames.PARKING_STALL));
	}
}
