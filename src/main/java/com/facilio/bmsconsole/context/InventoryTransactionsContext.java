package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;

public class InventoryTransactionsContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;
	
	private InventryContext inventory;
	public InventryContext getInventory() {
		return inventory;
	}
	public void setInventory(InventryContext inventory) {
		this.inventory = inventory;
	}
	
	private InventoryCostContext inventoryCost;
	public InventoryCostContext getInventoryCost() {
		return inventoryCost;
	}
	public void setInventoryCost(InventoryCostContext inventoryCost) {
		this.inventoryCost = inventoryCost;
	}
	
	private double quantity=-1;
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getQuantity() {
		return quantity;
	}
	
	private long parentId=-1;
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
		if(isReturnable != null) {
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
	
}
