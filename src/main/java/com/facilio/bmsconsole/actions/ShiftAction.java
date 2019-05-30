package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ShiftAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String add() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT, this.shift);
		Chain c = FacilioChainFactory.getAddShiftCommand();
		c.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIFT, context.get(FacilioConstants.ContextNames.SHIFT));

		return SUCCESS;
	}
	
	public String update() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT, this.shift);
		Chain c = FacilioChainFactory.getUpdateShiftCommand();
		c.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIFT, context.get(FacilioConstants.ContextNames.SHIFT));
		return SUCCESS;
	}
	
	public String all() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain c = FacilioChainFactory.getAllShiftsCommand();
		c.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIFTS, (List<ShiftContext>) context.get(FacilioConstants.ContextNames.SHIFTS));
		return SUCCESS;
	}
	
	public String delete() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, this.id);
		context.put(FacilioConstants.ContextNames.DO_VALIDTION, this.doValidation);
		
		Chain c = FacilioChainFactory.getDeleteShiftCommand();
		c.execute(context);
		
		this.users = (List<String>) context.get(FacilioConstants.ContextNames.USERS);
		
		return SUCCESS;
	}
	
	public String scheduleTesting() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT_ID, id);
		
		Chain chain = TransactionChainFactory.markAbsentChain();
		chain.execute(context);
		
		return SUCCESS;
	}
	
	private BreakContext breakContext;
	public BreakContext getBreakContext() {
		return breakContext;
	}
	public void setBreakContext(BreakContext breakContext) {
		this.breakContext = breakContext;
	}
	
	public String addOrUpdateBreak() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BREAK, breakContext);
		
		Chain chain = TransactionChainFactory.addOrUpdateBreakChain();
		chain.execute(context);
		return SUCCESS;
	}
	
	public String getAllBreak() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain c = ReadOnlyChainFactory.getAllBreakChain();
		c.execute(context);
		
		setResult(FacilioConstants.ContextNames.BREAK_LIST, context.get(FacilioConstants.ContextNames.BREAK_LIST));
		return SUCCESS;
	}
	
	public String getBreak() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		
		Chain c = ReadOnlyChainFactory.getBreakChain();
		c.execute(context);
		
		setResult("breakContext", context.get(FacilioConstants.ContextNames.BREAK));
		
		return SUCCESS;
	}
	
	private long startDate = -1;
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
	public long getStartDate() {
		return startDate;
	}
	
	private long endDate = -1;
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public long getEndDate() {
		return endDate;
	}
	
	public String getShiftUserMapping() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, startDate);
		context.put(FacilioConstants.ContextNames.END_TIME, endDate);
		
		Chain c = ReadOnlyChainFactory.getShiftUserMappingChain();
		c.execute(context);
		
		setResult(FacilioConstants.ContextNames.SHIFT_USER_MAPPING, context.get(FacilioConstants.ContextNames.SHIFT_USER_MAPPING));
		return SUCCESS;
	}
	
	public String addShiftUserMapping() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ORG_USER_ID, orgUserId);
		context.put(FacilioConstants.ContextNames.START_TIME, startDate);
		context.put(FacilioConstants.ContextNames.END_TIME, endDate);
		context.put(FacilioConstants.ContextNames.SHIFT_ID, id);
		
		Chain c = TransactionChainFactory.addShiftUserMappingChain();
		c.execute(context);
		
		return SUCCESS;
	}
	
	private long orgUserId = -1;
	public long getOrgUserId() {
		return orgUserId;
	}
	public void setOrgUserId(long orgUserId) {
		this.orgUserId = orgUserId;
	}
	
	private long id;
	private List<String> users;
	
	private boolean doValidation;
	public boolean getDoValidation() {
		return doValidation;
	}
	
	public void setDoValidation(boolean doValidation) {
		this.doValidation = doValidation;
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

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
