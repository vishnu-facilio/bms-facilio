package com.facilio.bmsconsoleV3.context.purchaseorder;

import com.facilio.bmsconsole.context.ReceivableContext.Status;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.v3.context.V3Context;

public class V3ReceivableContext extends V3Context {
	
	private static final long serialVersionUID = 1L;

	private Status status;
	private Long requiredTime;
	private V3PurchaseOrderContext poId;

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
		YET_TO_RECEIVE,
		PARTIALLY_RECEIVED,
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

	public Long getRequiredTime() {
		return requiredTime;
	}
	public void setRequiredTime(Long requiredTime) {
		this.requiredTime = requiredTime;
	}
	public V3PurchaseOrderContext getPoId() {
		return poId;
	}
	public void setPoId(V3PurchaseOrderContext poId) {
		this.poId = poId;
	}
}
