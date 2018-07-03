package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class ShiftAction extends ActionSupport {
	
	public String add() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT, this.shift);
		Chain c = FacilioChainFactory.getAddShiftCommand();
		c.execute(context);

		return SUCCESS;
	}
	
	public String update() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT, this.shift);
		Chain c = FacilioChainFactory.getUpdateShiftCommand();
		c.execute(context);
		
		return SUCCESS;
	}
	
	public String all() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain c = FacilioChainFactory.getAllShiftsCommand();
		c.execute(context);
		
		this.shifts = (List<ShiftContext>) context.get(FacilioConstants.ContextNames.SHIFTS);
		
		return SUCCESS;
	}

	private ShiftContext shift;
	public void setShift(ShiftContext shift) {
		this.shift = shift;
	}
	
	public ShiftContext getShift() {
		return this.shift;
	}
	
	private List<ShiftContext> shifts;
	public void setShifts(List<ShiftContext> shifts) {
		this.shifts = shifts;
	}
	
	public List<ShiftContext> getShifts() {
		return this.shifts;
	}
	
}
