package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.List;

public class PoLineItemsSerialNumberContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;
	
	private PurchaseOrderLineItemContext lineItem;
	public PurchaseOrderLineItemContext getLineItem() {
		return lineItem;
	}
	public void setLineItem(PurchaseOrderLineItemContext lineItem) {
		this.lineItem = lineItem;
	}
	
	private long poId;
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}
	
	private long receiptId;
	public long getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(long receiptId) {
		this.receiptId = receiptId;
	}
	
	private String serialNumber;
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	private List<String> serialNumbers;
	public List<String> getSerialNumbers() {
		return serialNumbers;
	}
	public void setSerialNumbers(List<String> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}
	
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
	private List<AssetContext> assets;
	public List<AssetContext> getAssets() {
		return assets;
	}
	public void setAssets(List<AssetContext> assets) {
		this.assets = assets;
	}
}
