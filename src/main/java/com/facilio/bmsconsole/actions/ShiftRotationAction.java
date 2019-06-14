package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.context.ShiftRotationDetailsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ShiftRotationAction extends ModuleAction{

	private static final long serialVersionUID = 1L;
	private ShiftRotationContext shiftRotation;
	public ShiftRotationContext getShiftRotation() {
		return shiftRotation;
	}
	public void setShiftRotation(ShiftRotationContext shiftRotation) {
		this.shiftRotation = shiftRotation;
	}
	
	private List<ShiftRotationDetailsContext> shiftRotationDetails;
	public List<ShiftRotationDetailsContext> getShiftRotationDetails() {
		return shiftRotationDetails;
	}
	public void setShiftRotationDetails(List<ShiftRotationDetailsContext> shiftRotationDetails) {
		this.shiftRotationDetails = shiftRotationDetails;
	}
	
	private List<Long> users;
	public List<Long> getUsers() {
		return users;
	}
	public void setUsers(List<Long> users) {
		this.users = users;
	}
	
	public String addShiftRotation() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shiftRotation);
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION_APPLICABLE_FOR, shiftRotation.getApplicableFor());
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION_DETAILS, shiftRotation.getShiftRotations());
		
		Chain c = TransactionChainFactory.getAddShiftRotationChain();
		c.execute(context);
		
		return SUCCESS;
	}
	
	private long shiftRotationId;
	public long getShiftRotationId() {
		return shiftRotationId;
	}
	public void setShiftRotationId(long shiftRotationId) {
		this.shiftRotationId = shiftRotationId;
	}
	
	public String executeShiftRotation() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, shiftRotation);
		
		Chain c = TransactionChainFactory.getExecuteShiftRotationCommand();
		c.execute(context);
		
		return SUCCESS;
	}

}
