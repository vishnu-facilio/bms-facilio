package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ReceiptContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	
	private long receivableId = -1;
	private long lineItemId = -1;
	
	public long getLineItemId() {
		return lineItemId;
	}
	public void setLineItemId(long lineItemId) {
		this.lineItemId = lineItemId;
	}
	public long getReceivableId() {
		return receivableId;
	}
	public void setReceivableId(long receivableId) {
		this.receivableId = receivableId;
	}
	
	private Status status;
	public int	getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public Status getStatusEnum() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	public static enum Status {
		RECEIVED,
		RETURNED;
		
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
