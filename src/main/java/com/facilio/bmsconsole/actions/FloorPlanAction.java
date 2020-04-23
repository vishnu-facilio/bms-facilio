package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.bmsconsole.floorplan.FloorPlanMode;
import com.facilio.bmsconsole.floorplan.FloorPlanViewContext;
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
	public String getAllFloorPlanData() throws Exception {
		FacilioChain c = TransactionChainFactory.getAllFloorPlanChain();
		c.execute();
		setResult(FacilioConstants.ContextNames.FLOOR_PLANS, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLANS));
		return SUCCESS;
	}
	
	public String getAvailableViewModes() {
		
		List<FloorPlanMode> availableViewModes = new ArrayList<>();
		availableViewModes.add(FloorPlanMode.DEFAULT);
		availableViewModes.add(FloorPlanMode.SPACE_CATEGORY);
		setResult("availableViewModes", availableViewModes);
		return SUCCESS;
	}
	
	private FloorPlanViewContext floorPlanViewMode;
	
	public FloorPlanViewContext getFloorPlanViewMode() {
		return floorPlanViewMode;
	}
	
	public void setFloorPlanViewMode(FloorPlanViewContext floorPlanViewMode) {
		this.floorPlanViewMode = floorPlanViewMode;
	}
	
	public String viewFloorPlanMode() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getExecuteFloorPlanWorkflowChain();
		chain.getContext().put(FacilioConstants.ContextNames.FLOORPLAN_VIEW_CONTEXT, floorPlanViewMode);
		
		chain.execute();
			
		setResult("floorPlanViewMode", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
		setResult("data", chain.getContext().get(FacilioConstants.ContextNames.RESULT));
		setResult("state", ((FloorPlanViewContext) chain.getContext().get(FacilioConstants.ContextNames.FLOORPLAN_VIEW_CONTEXT)).getViewState());
		return SUCCESS;
	}
}
