package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;

import java.util.List;

public class ItemTransactionsContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private ItemTypesContext itemType;

	public ItemTypesContext getItemType() {
		return itemType;
	}

	public void setItemType(ItemTypesContext itemType) {
		this.itemType = itemType;
	}

	private ItemContext item;

	public ItemContext getItem() {
		return item;
	}

	public void setItem(ItemContext inventory) {
		this.item = inventory;
	}

	private PurchasedItemContext purchasedItem;

	public PurchasedItemContext getPurchasedItem() {
		return purchasedItem;
	}

	public void setPurchasedItem(PurchasedItemContext inventoryCost) {
		this.purchasedItem = inventoryCost;
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

	private long parentTransactionId = -1;

	public long getParentTransactionId() {
		return parentTransactionId;
	}

	public void setParentTransactionId(long parentTransactionId) {
		this.parentTransactionId = parentTransactionId;
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
	
	private List<Long> purchasedItems;
	public List<Long> getPurchasedItems() {
		return purchasedItems;
	}
	public void setPurchasedItems(List<Long> purchasedItems) {
		this.purchasedItems = purchasedItems;
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
	
	private InventoryRequestLineItemContext requestedLineItem;
	public InventoryRequestLineItemContext getRequestedLineItem() {
		return requestedLineItem;
	}
	public void setRequestedLineItem(InventoryRequestLineItemContext requestedLineItem) {
		this.requestedLineItem = requestedLineItem;
	}
	
}
