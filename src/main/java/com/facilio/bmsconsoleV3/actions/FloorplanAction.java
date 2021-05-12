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
}
