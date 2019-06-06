package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ReceiptContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	
	private long receivableId = -1;
	private PurchaseOrderLineItemContext lineItem;
	
	public PurchaseOrderLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseOrderLineItemContext lineItem) {
		this.lineItem = lineItem;
	}

	private double quantity = -1;
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
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
	
	private long receiptTime = -1;
	public long getReceiptTime() {
		return receiptTime;
	}
	public void setReceiptTime(long receiptTime) {
		this.receiptTime = receiptTime;
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
	
	private String remarks;
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	private int noOfSerialNumbers = -1;
	public int getNoOfSerialNumbers() {
		return noOfSerialNumbers;
	}
	public void setNoOfSerialNumbers(int noOfSerialNumbers) {
		this.noOfSerialNumbers = noOfSerialNumbers;
	}

}
