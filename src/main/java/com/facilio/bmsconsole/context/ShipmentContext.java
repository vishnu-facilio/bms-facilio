package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PurchaseOrderContext.Status;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ShipmentContext extends ModuleBaseWithCustomFields{
	private User transferredBy;
	public User getTransferredBy() {
		return transferredBy;
	}
	public void setTransferredBy(User transferredBy) {
		this.transferredBy = transferredBy;
	}
	
	private User receivedBy;
	public User getReceivedBy() {
		return receivedBy;
	}
	public void setReceivedBy(User receivedBy) {
		this.receivedBy = receivedBy;
	}
	
	private StoreRoomContext fromStore;
	public StoreRoomContext getFromStore() {
		return fromStore;
	}
	public void setFromStore(StoreRoomContext fromStore) {
		this.fromStore = fromStore;
	}
	
	private StoreRoomContext toStore;
	public StoreRoomContext getToStore() {
		return toStore;
	}
	public void setToStore(StoreRoomContext toStore) {
		this.toStore = toStore;
	}
	
	private Status status;
	public Status getStatusEnum() {
		return status;
	}

	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	public static enum Status {
		NOTSTAGED(),
		STAGED(),
		SHIPPED()
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
}
