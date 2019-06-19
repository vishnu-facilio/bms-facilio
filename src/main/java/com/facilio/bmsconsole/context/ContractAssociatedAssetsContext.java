package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ContractAssociatedAssetsContext extends ModuleBaseWithCustomFields {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long contractId;
	private AssetContext asset;
	
	private Status status;
	public Status getStatusEnum() {
		return status;
	}
	public int getStatusType() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public static enum Status {
		PURCHASED(),
		RETURNED()
		
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
	
	private double totalCost;
	private double leaseEndValue;
	public long getContractId() {
		return contractId;
	}
	public void setContractId(long contractId) {
		this.contractId = contractId;
	}
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getLeaseEndValue() {
		return leaseEndValue;
	}
	public void setLeaseEndValue(double leaseEndValue) {
		this.leaseEndValue = leaseEndValue;
	}
	
	
}
