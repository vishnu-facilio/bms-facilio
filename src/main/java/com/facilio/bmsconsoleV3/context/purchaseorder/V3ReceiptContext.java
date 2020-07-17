package com.facilio.bmsconsoleV3.context.purchaseorder;

import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.context.ReceiptContext.Status;
import com.facilio.v3.context.V3Context;

public class V3ReceiptContext extends V3Context {
	
	private static final long serialVersionUID = 1L;
	
	private V3PurchaseOrderLineItemContext lineItem;
	private Long receivableId;
	private Double quantity;
	private Long receiptTime;
	private Status status;
	private String remarks;
	private Integer noOfSerialNumbers;

	public V3PurchaseOrderLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(V3PurchaseOrderLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	public Long getReceivableId() {
		return receivableId;
	}
	public void setReceivableId(Long receivableId) {
		this.receivableId = receivableId;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Long getReceiptTime() {
		return receiptTime;
	}
	public void setReceiptTime(Long receiptTime) {
		this.receiptTime = receiptTime;
	}
	public Integer getNoOfSerialNumbers() {
		return noOfSerialNumbers;
	}
	public void setNoOfSerialNumbers(Integer noOfSerialNumbers) {
		this.noOfSerialNumbers = noOfSerialNumbers;
	}
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
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
