package com.facilio.bmsconsoleV3.context.purchaseorder;

import com.facilio.bmsconsole.context.ReceiptLineItemContext.Status;
import com.facilio.v3.context.V3Context;

public class V3ReceiptLineItemContext extends V3Context {
	
	private static final long serialVersionUID = 1L;

	private Long receiptId;
	private Long itemId;
	private Double quantity;
	private Status status;

	public Long getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(Long receiptId) {
		this.receiptId = receiptId;
	}
	public Long getItemId() {
		return itemId;
	}
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public int getStatus() {
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
		COMPLETED;
		
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
