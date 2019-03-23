package com.facilio.bmsconsole.context;

public class PurchaseRequestPurchaseOrderContext {
	
	private static final long serialVersionUID = 1L;
	
	
	private long poId = -1;
	private long prId = -1;
	
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}
	public long getPrId() {
		return prId;
	}
	public void setPrId(long prId) {
		this.prId = prId;
	}

}
