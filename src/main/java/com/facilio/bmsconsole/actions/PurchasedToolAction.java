package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class PurchasedToolAction extends FacilioAction{
	private static final long serialVersionUID = 1L;
	
	private PurchasedToolContext purchasedTool;
	public PurchasedToolContext getPurchasedTool() {
		return purchasedTool;
	}
	public void setPurchasedTool(PurchasedToolContext purchasedTool) {
		this.purchasedTool = purchasedTool;
	}
	
	private List<PurchasedToolContext> purchasedTools;
	public List<PurchasedToolContext> getPurchasedTools() {
		return purchasedTools;
	}
	public void setPurchasedTools(List<PurchasedToolContext> purchasedTools) {
		this.purchasedTools = purchasedTools;
	}
	
	private long purchasedToold;
	public long getPurchasedToold() {
		return purchasedToold;
	}
	public void setPurchasedToold(long inventoryCostId) {
		this.purchasedToold = inventoryCostId;
	}
	
	private long toolId;
	public long getToolId() {
		return toolId;
	}
	public void setToolId(long inventoryId) {
		this.toolId = inventoryId;
	}
	
	public String addPurchasedItem() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD, purchasedTools);
		context.put(FacilioConstants.ContextNames.TOOL_ID, toolId);
		context.put(FacilioConstants.ContextNames.RECORD_ID, toolId);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		Chain addPurchasedTool = TransactionChainFactory.getAddPurchasedToolChain();
		addPurchasedTool.execute(context);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		setResult(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		return SUCCESS;
	}
	
	public String purchasedToolsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, toolId);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL_IS_USED, true);
		Chain purchasedItemsListChain = ReadOnlyChainFactory.getPurchasdToolsList();
		purchasedItemsListChain.execute(context);
		purchasedTools = ((List<PurchasedToolContext>) context.get(FacilioConstants.ContextNames.PURCHASED_TOOL));
		setResult(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		return SUCCESS;
	}

	public String unUsedPurchasedToolsList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.PARENT_ID, toolId);
		context.put(FacilioConstants.ContextNames.PURCHASED_TOOL_IS_USED, false);
		Chain purchasedItemsListChain = ReadOnlyChainFactory.getPurchasdToolsList();
		purchasedItemsListChain.execute(context);
		purchasedTools = ((List<PurchasedToolContext>) context.get(FacilioConstants.ContextNames.PURCHASED_TOOL));
		setResult(FacilioConstants.ContextNames.PURCHASED_TOOL, purchasedTools);
		return SUCCESS;
	}
}
