package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.IndoorFloorPlanContext;
import com.facilio.bmsconsole.context.IndoorFloorPlanObjectContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class IndoorFloorPlanAction extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	
	private IndoorFloorPlanContext indoorFloorPlan;


	public IndoorFloorPlanContext getIndoorFloorPlan() {
		return indoorFloorPlan;
	}

	public void setIndoorFloorPlan(IndoorFloorPlanContext indoorFloorPlan) {
		this.indoorFloorPlan = indoorFloorPlan;
	}

	public String addFloorPlan() throws Exception {
		

		FacilioChain c = TransactionChainFactory.addIndoorFloorPlanChain();
		

		c.getContext().put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN,indoorFloorPlan);
        List<IndoorFloorPlanContext> floorPlanObjects  = (List<IndoorFloorPlanContext>) c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);
		c.getContext().put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS,floorPlanObjects);
		c.execute();
		setResult(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN));
		return SUCCESS;
	}
	
	public String getFloorPlanById() throws Exception {
		FacilioChain c = TransactionChainFactory.getIndoorFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN,indoorFloorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN));
		return SUCCESS;
	}
	
	public String updateFloorPlan() throws Exception {
		FacilioChain c = TransactionChainFactory.updateIndoorFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN,indoorFloorPlan);
        List<IndoorFloorPlanContext> floorPlanObjects  = (List<IndoorFloorPlanContext>) c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);
		c.getContext().put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS,floorPlanObjects);
		c.execute();
		setResult(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN));
		return SUCCESS;
	}
	
	
	public String deleteFloorPlan() throws Exception {
		FacilioChain c = TransactionChainFactory.deleteIndoorFloorPlanChain();
		c.getContext().put(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN,indoorFloorPlan);
		c.execute();
		setResult(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN));
		return SUCCESS;
	}
	
	
	public String getAllFloorPlan() throws Exception {
		FacilioChain c = TransactionChainFactory.getAllIndoorFloorPlanChain();
		c.execute();
		setResult(FacilioConstants.ContextNames.INDOOR_FLOOR_PLANS, c.getContext().get(FacilioConstants.ContextNames.INDOOR_FLOOR_PLANS));
		return SUCCESS;
	}
	
}
