package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.context.ContractsContext.Status;

public class WarrantyContractContext extends ContractsContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WarrantyContractContext() {
		// TODO Auto-generated constructor stub
	}
	
	public WarrantyContractContext(WarrantyContractContext warrantyContract) {
		super(warrantyContract);
		this.lineItems = warrantyContract.lineItems;
	}
	
	private List<WarrantyContractLineItemContext> lineItems;
	public List<WarrantyContractLineItemContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<WarrantyContractLineItemContext> lineItems) {
		this.lineItems = lineItems;
	}
	
	private WarrantyContractType warrantyContractType;
	public WarrantyContractType getWarrantyContractTypeEnum() {
		return warrantyContractType;
	}
	public int getWarrantyContractType() {
		if (warrantyContractType != null) {
			return warrantyContractType.getValue();
		}
		return -1;
	}
	public void setWarrantyContractType(WarrantyContractType warrantyContractType) {
		this.warrantyContractType = warrantyContractType;
	}
	public void setWarrantyContractType(int warrantyContractType) {
		this.warrantyContractType = WarrantyContractType.valueOf(warrantyContractType);
	}
	
	public static enum WarrantyContractType {
		SERVICE(),
		WARRANTY()
		
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static WarrantyContractType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private List<Long> assetIds;
	public List<Long> getAssetIds() {
		return assetIds;
	}
	public void setAssetIds(List<Long> assetIds) {
		this.assetIds = assetIds;
	}
	
	private List<AssetContext> associatedAssets;
	
	
	public List<AssetContext> getAssociatedAssets() {
		return associatedAssets;
	}

	public void setAssociatedAssets(List<AssetContext> associatedAssets) {
		this.associatedAssets = associatedAssets;
	}

	public WarrantyContractContext clone() {
		WarrantyContractContext warranty = new WarrantyContractContext(this);
		warranty.setWarrantyContractType(this.getWarrantyContractType());
		warranty.setAssetIds(this.getAssetIds());

		return warranty;
	}
}
