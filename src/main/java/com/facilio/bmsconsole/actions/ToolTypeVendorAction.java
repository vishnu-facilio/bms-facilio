package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ToolTypeVendorContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ToolTypeVendorAction extends FacilioAction{

	private static final long serialVersionUID = 1L;

	private List<ToolTypeVendorContext> toolTypesVendors;
	
	
	public List<ToolTypeVendorContext> getToolTypesVendors() {
		return toolTypesVendors;
	}
	public void setToolTypesVendors(List<ToolTypeVendorContext> toolTypesVendors) {
		this.toolTypesVendors = toolTypesVendors;
	}

	private List<Long> toolVendorsId;
	
	public List<Long> getToolVendorsId() {
		return toolVendorsId;
	}
	public void setToolVendorsId(List<Long> toolVendorsId) {
		this.toolVendorsId = toolVendorsId;
	}

	private long toolTypesId;
	
	public long getToolTypesId() {
		return toolTypesId;
	}
	public void setToolTypesId(long toolTypesId) {
		this.toolTypesId = toolTypesId;
	}

	private long vendorsId;
	public long getVendorsId() {
		return vendorsId;
	}
	public void setVendorsId(long vendorsId) {
		this.vendorsId = vendorsId;
	}
	
	public String addToolVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTypesVendors);
		Chain addWorkorderPartChain = TransactionChainFactory.getAddToolTypesVendorsChain();
		addWorkorderPartChain.execute(context);
		setToolVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("toolsVendorsId", toolVendorsId);
		return SUCCESS;
	}
	
	public String udpateToolVendors() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		context.put(FacilioConstants.ContextNames.RECORD_LIST, toolTypesVendors);
		Chain addWorkorderPartChain = TransactionChainFactory.getUpdateToolTypesVendorsChain();
		addWorkorderPartChain.execute(context);
		setToolVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("toolsVendorsId", toolVendorsId);
		return SUCCESS;
	}
	
	public String deleteToolVendors() throws Exception {
		FacilioContext context = new FacilioContext();
	
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.PARENT_ID, toolTypesId);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, toolVendorsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeleteToolTypesVendorsChain();
		deleteInventoryChain.execute(context);
		setToolVendorsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		setResult("toolVendorsId", toolVendorsId);
		return SUCCESS;
	}
	
	public String toolVendorsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TOOL_TYPES_ID, toolTypesId);
		Command toolVendorList = ReadOnlyChainFactory.getToolVendorsList();
		toolVendorList.execute(context);
		toolTypesVendors = ((List<ToolTypeVendorContext>) context.get(FacilioConstants.ContextNames.TOOL_VENDORS));
		setResult(FacilioConstants.ContextNames.TOOL_VENDORS, toolTypesVendors);
		return SUCCESS;
	}
	
	
	public String toolTypesForVendorsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.VENDOR_ID, vendorsId);
		Command toolVendorList = ReadOnlyChainFactory.GetToolTypesForVendorCommand();
		toolVendorList.execute(context);
		toolTypesVendors = ((List<ToolTypeVendorContext>) context.get(FacilioConstants.ContextNames.TOOL_VENDORS));
		setResult(FacilioConstants.ContextNames.TOOL_VENDORS, toolTypesVendors);
		return SUCCESS;
	}
}
