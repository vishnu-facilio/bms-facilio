package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class FloorPlanAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	
	private FloorPlanContext floorPlan;

	public FloorPlanContext getFloorPlan() {
		return floorPlan;
	}

	public void setFloorPlan(FloorPlanContext floorPlan) {
		this.floorPlan = floorPlan;
	}
	
	public String addFloorPlan() throws Exception {
		FacilioChain c = TransactionChainFactory.addFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN,floorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN));
		return SUCCESS;
	}
	
	public String getFloorPlanData() throws Exception {
		FacilioChain c = TransactionChainFactory.getFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN,floorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN));
		return SUCCESS;
	}
	
	private long floorId;
	
	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}
	
	public long getFloorId() {
		return this.floorId;
	}
	
	private long floorPlanId;
	
	public void setFloorPlanId(long floorPlanId) {
		this.floorPlanId = floorPlanId;
	}
	
	public long getFloorPlanId() {
		return this.floorPlanId;
	}
	
	public String getFloorPlanSpaces() throws Exception {
		
		FacilioChain c = ReadOnlyChainFactory.getFloorPlanSpacesChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_ID, getFloorId());
		c.execute();
		
		setResult(FacilioConstants.ContextNames.SPACE_LIST, c.getContext().get(FacilioConstants.ContextNames.SPACE_LIST));
		
		return SUCCESS;
	}
	
	public String getListOfFloorPlan() throws Exception {
		FacilioChain c = TransactionChainFactory.getListOfFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN,floorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.FLOOR_PLANS, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLANS));
		return SUCCESS;
	}
	
	public String updateFloorPlanData() throws Exception {
		FacilioChain c = TransactionChainFactory.updateFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN,floorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN));
		return SUCCESS;
	}
	
	public String deleteFloorPlan() throws Exception {
		FacilioChain c = TransactionChainFactory.deleteFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN,floorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN));
		return SUCCESS;
	}
}
