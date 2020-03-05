package com.facilio.bmsconsole.actions;

import java.util.Collections;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.FloorPlanContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.time.DateTimeUtil;


public class FloorPlanAction  extends FacilioAction {
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
public String updateFloorPlanData() throws Exception {
	FacilioChain c = TransactionChainFactory.updateFloorPlanChain();
	c.getContext().put(FacilioConstants.ContextNames.FLOOR_PLAN,floorPlan);
	c.execute();
	setResult(FacilioConstants.ContextNames.FLOOR_PLAN, c.getContext().get(FacilioConstants.ContextNames.FLOOR_PLAN));
	return SUCCESS;
}
}
