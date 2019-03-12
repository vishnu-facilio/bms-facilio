package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PurchasedItemContext;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
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
	
	private long purchasedToolId;
	public long getPurchasedToolId() {
		return purchasedToolId;
	}
	public void setPurchasedToolId(long inventoryCostId) {
		this.purchasedToolId = inventoryCostId;
	}
	
	private long toolId;
	public long getToolId() {
		return toolId;
	}
	public void setToolId(long inventoryId) {
		this.toolId = inventoryId;
	}
	
	private List<Long> purchasedToolsId;
	public List<Long> getPurchasedToolsId() {
		return purchasedToolsId;
	}
	public void setPurchasedToolsId(List<Long> inventoryCostsId) {
		this.purchasedToolsId = inventoryCostsId;
	}
	
	
	public String addPurchasedTool() throws Exception {
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
	
	public String deletePurchasedTool() throws Exception {
		FacilioContext context = new FacilioContext();
		PurchasedToolContext purchasedtool = new PurchasedToolContext();
		purchasedtool.setDeleted(true);

		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD, purchasedtool);
		context.put(FacilioConstants.ContextNames.TOOL_ID, toolId);
		context.put(FacilioConstants.ContextNames.TOOL_IDS, Collections.singletonList(toolId));
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, purchasedToolsId);

		Chain deleteInventoryChain = TransactionChainFactory.getDeletePurchasedToolsChain();
		deleteInventoryChain.execute(context);
		setPurchasedToolsId((List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		setResult("inventoryCostsId", purchasedToolsId);
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
	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}

	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
}
