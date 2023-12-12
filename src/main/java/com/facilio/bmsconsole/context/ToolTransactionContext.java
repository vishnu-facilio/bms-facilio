package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3BinContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ToolTransactionContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private ToolTypesContext toolType;

	public ToolTypesContext getToolType() {
		return toolType;
	}

	public void setToolType(ToolTypesContext toolType) {
		this.toolType = toolType;
	}

	private PurchasedToolContext purchasedTool;

	public PurchasedToolContext getPurchasedTool() {
		return purchasedTool;
	}

	public void setPurchasedTool(PurchasedToolContext purchasedTool) {
		this.purchasedTool = purchasedTool;
	}

	private ToolContext tool;

	public ToolContext getTool() {
		return tool;
	}

	public void setTool(ToolContext stockedTool) {
		this.tool = stockedTool;
	}

	private double quantity = -1;

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getQuantity() {
		return quantity;
	}

	private long parentId = -1;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private long parentTransactionId = -1;

	public long getParentTransactionId() {
		return parentTransactionId;
	}

	public void setParentTransactionId(long parentTransactionId) {
		this.parentTransactionId = parentTransactionId;
	}

	private TransactionType transactionType;

	public TransactionType getTransactionTypeEnum() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public int getTransactionType() {
		if (transactionType != null) {
			return transactionType.getValue();
		}
		return -1;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = TransactionType.valueOf(transactionType);
	}

	private TransactionState transactionState;

	public TransactionState getTransactionStateEnum() {
		return transactionState;
	}

	public void setTransactionState(TransactionState transactionState) {
		this.transactionState = transactionState;
	}

	public int getTransactionState() {
		if (transactionState != null) {
			return transactionState.getValue();
		}
		return -1;
	}

	public void setTransactionState(int transactionState) {
		this.transactionState = TransactionState.valueOf(transactionState);
	}

	private Boolean isReturnable;

	public Boolean getIsReturnable() {
		return isReturnable;
	}

	public void setIsReturnable(Boolean isReturnable) {
		this.isReturnable = isReturnable;
	}

	public boolean isReturnable() {
		if (isReturnable != null) {
			return isReturnable.booleanValue();
		}
		return false;
	}

	private User issuedTo;

	public User getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(User owner) {
		this.issuedTo = owner;
	}

	private double remainingQuantity = -1;

	public double getRemainingQuantity() {
		return remainingQuantity;
	}

	public void setRemainingQuantity(double remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}
	
	private List<Long> purchasedTools;
	public List<Long> getPurchasedTools() {
		return purchasedTools;
	}
	public void setPurchasedTools(List<Long> purchasedTools) {
		this.purchasedTools = purchasedTools;
	}
	
	private ApprovalState approvedState;

	public ApprovalState getApprovedStateEnum() {
		return approvedState;
	}

	public void setApprovedState(ApprovalState approvedState) {
		this.approvedState = approvedState;
	}

	public int getApprovedState() {
		if (approvedState != null) {
			return approvedState.getValue();
		}
		return -1;
	}
	
	public void setApprovedState(int approvedState) {
		this.approvedState = ApprovalState.valueOf(approvedState);
	}
	
	private TicketContext workorder;
	public TicketContext getWorkorder() {
		return workorder;
	}
	public void setWorkorder(TicketContext workorder) {
		this.workorder = workorder;
	}
	
	private GatePassContext gatePass;
	public GatePassContext getGatePass() {
		return gatePass;
	}
	public void setGatePass(GatePassContext gatePass) {
		this.gatePass = gatePass;
	}
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
	private List<Long> assetIds;
	public List<Long> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Long> assetIds) {
		this.assetIds = assetIds;
	}

	private V3InventoryRequestLineItemContext requestedLineItem;
	public V3InventoryRequestLineItemContext getRequestedLineItem() {
		return requestedLineItem;
	}
	public void setRequestedLineItem(V3InventoryRequestLineItemContext requestedLineItem) {
		this.requestedLineItem = requestedLineItem;
	}
	private V3BinContext bin;

	public V3BinContext getBin() {
		return bin;
	}

	public void setBin(V3BinContext bin) {
		this.bin = bin;
	}
	private ResourceContext resource;

	public ResourceContext getResource() {
		return resource;
	}

	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}

	private long shipment;

	public long getShipment() {
		return shipment;
	}

	public void setShipment(long shipment) {
		this.shipment = shipment;
	}

	private double transactionCost;

	public double getTransactionCost() {
		return transactionCost;
	}

	public void setTransactionCost(double transactionCost) {
		this.transactionCost = transactionCost;
	}
	
	private StoreRoomContext storeRoom;

	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}

	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}
}
